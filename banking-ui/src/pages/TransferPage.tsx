import { useState } from 'react';
import { sendTransfer } from '../api/transferApi';
import { ActionCard } from '../components/ActionCard';
import { ErrorAlert } from '../components/ErrorAlert';
import { FormSection } from '../components/FormSection';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoTransfer } from '../data/demoData';
import type { TransferRequest, TransferResponse } from '../types/transfer';
import { useAppState } from '../utils/appState';
import { formatCurrency, toDebugValue } from '../utils/format';
import { generateIdempotencyKey } from '../utils/idempotency';

const initialTransferForm: TransferRequest = {
  fromAccountNo: '',
  toAccountNo: '',
  amount: '',
  currency: 'VND',
};

export function TransferPage() {
  const { setLastTransfer } = useAppState();
  const [form, setForm] = useState<TransferRequest>(initialTransferForm);
  const [idempotencyKey, setIdempotencyKey] = useState(generateIdempotencyKey());
  const [lastSubmittedRequest, setLastSubmittedRequest] = useState<TransferRequest | null>(null);
  const [lastSubmittedKey, setLastSubmittedKey] = useState('');
  const [transferResult, setTransferResult] = useState<TransferResponse | null>(null);
  const [rawJson, setRawJson] = useState<unknown>({ note: 'Transfer responses and errors will appear here.' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<unknown>(null);

  async function submitTransfer(request: TransferRequest, key: string) {
    setLoading(true);
    setError('');

    try {
      const response = await sendTransfer(request, key);
      setTransferResult(response.data);
      setLastTransfer(response.data);
      setLastSubmittedRequest(request);
      setLastSubmittedKey(key);
      setRawJson(response);
    } catch (requestError) {
      setError(requestError);
      setRawJson(toDebugValue(requestError));
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await submitTransfer(form, idempotencyKey);
  }

  async function handleRepeatRequest() {
    if (!lastSubmittedRequest || !lastSubmittedKey) {
      return;
    }

    await submitTransfer(lastSubmittedRequest, lastSubmittedKey);
  }

  return (
    <div className="page-grid">
      <PageHeader
        title="Transfer"
        description="Submit transfer requests with an explicit Idempotency-Key and compare live responses against the expected duplicate-safe flow."
        helper="If you send the same body with the same Idempotency-Key, the backend should return the stored response instead of executing the transfer again."
        actions={<StatusBadge status="Idempotency lab" tone="info" subtle />}
      />

      {error ? <ErrorAlert error={error} /> : null}
      {loading ? <LoadingState label="Submitting transfer request..." /> : null}

      <FormSection
        title="Send transfer"
        description="Uses POST /api/v1/transfers and passes the Idempotency-Key in the request header."
        badge={<StatusBadge status="Live transfer call" tone="warning" subtle />}
      >
        <form className="form-grid" onSubmit={handleSubmit}>
          <label>
            From Account No
            <input
              value={form.fromAccountNo}
              onChange={(event) => setForm((current) => ({ ...current, fromAccountNo: event.target.value }))}
              placeholder="100001"
            />
          </label>
          <label>
            To Account No
            <input
              value={form.toAccountNo}
              onChange={(event) => setForm((current) => ({ ...current, toAccountNo: event.target.value }))}
              placeholder="100002"
            />
          </label>
          <label>
            Amount
            <input
              value={form.amount}
              onChange={(event) => setForm((current) => ({ ...current, amount: event.target.value }))}
              placeholder="50000"
              inputMode="decimal"
            />
          </label>
          <label>
            Currency
            <input
              value={form.currency}
              onChange={(event) => setForm((current) => ({ ...current, currency: event.target.value }))}
              placeholder="VND"
            />
          </label>
          <label className="form-grid__full">
            Idempotency-Key
            <input value={idempotencyKey} onChange={(event) => setIdempotencyKey(event.target.value)} />
          </label>
          <p className="helper-text form-grid__full">
            Reusing the same key with the same request body should return the same stored response. Reusing the same key with a different body should be rejected by the backend.
          </p>
          <div className="button-row form-grid__full">
            <button
              className="button"
              type="button"
              onClick={() => setIdempotencyKey(generateIdempotencyKey())}
            >
              Generate key
            </button>
            <button className="button button--primary" type="submit">
              Send transfer
            </button>
            <button className="button" type="button" onClick={handleRepeatRequest} disabled={!lastSubmittedRequest}>
              Send same request again
            </button>
          </div>
        </form>
      </FormSection>

      <div className="action-grid action-grid--wide">
        <ActionCard
          title="Why idempotency matters"
          description="Network retries should not debit the source account twice. Reusing the same key lets the backend replay the stored result safely."
          meta={<StatusBadge status="Learning note" tone="neutral" subtle />}
        />
        <ActionCard
          title="Recommended test"
          description="Send a transfer, then click 'Send same request again' without changing the key. The response should stay stable instead of creating a second posting."
          meta={<StatusBadge status="Manual QA" tone="info" subtle />}
        />
      </div>

      {transferResult ? (
        <section className="result-card card">
          <div className="card-heading">
            <div>
              <h4>Transfer response</h4>
              <p>Latest backend result from the transfer endpoint.</p>
            </div>
            <StatusBadge status="Live response" tone="success" subtle />
          </div>
          <div className="result-grid">
            <div>
              <span>Reference No</span>
              <strong>{transferResult.referenceNo}</strong>
            </div>
            <div>
              <span>Status</span>
              <strong>{transferResult.status}</strong>
            </div>
            <div>
              <span>From</span>
              <strong>{transferResult.fromAccountNo}</strong>
            </div>
            <div>
              <span>To</span>
              <strong>{transferResult.toAccountNo}</strong>
            </div>
            <div>
              <span>Amount</span>
              <strong>{formatCurrency(transferResult.amount, transferResult.currency)}</strong>
            </div>
            <div>
              <span>Idempotency-Key</span>
              <strong>{lastSubmittedKey}</strong>
            </div>
          </div>
        </section>
      ) : (
        <section className="result-card card">
          <div className="card-heading">
            <div>
              <h4>Transfer response preview</h4>
              <p>Static demo panel used when a live response has not been received yet.</p>
            </div>
            <StatusBadge status="Static demo" tone="info" subtle />
          </div>
          <div className="result-grid">
            <div>
              <span>Reference No</span>
              <strong>{demoTransfer.referenceNo}</strong>
            </div>
            <div>
              <span>Status</span>
              <strong>{demoTransfer.status}</strong>
            </div>
            <div>
              <span>From</span>
              <strong>{demoTransfer.fromAccountNo}</strong>
            </div>
            <div>
              <span>To</span>
              <strong>{demoTransfer.toAccountNo}</strong>
            </div>
            <div>
              <span>Amount</span>
              <strong>{formatCurrency(demoTransfer.amount, demoTransfer.currency)}</strong>
            </div>
            <div>
              <span>Idempotency-Key</span>
              <strong>{idempotencyKey}</strong>
            </div>
          </div>
        </section>
      )}

      <JsonViewer value={rawJson} badgeLabel={transferResult ? 'Live payload' : 'Waiting for live call'} />
    </div>
  );
}
