import { useState } from 'react';
import { createAccount, getAccount } from '../api/accountApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { ErrorAlert } from '../components/ErrorAlert';
import { FormSection } from '../components/FormSection';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoAccounts } from '../data/demoData';
import type { AccountResponse, CreateAccountRequest } from '../types/account';
import { formatCurrency, formatDateTime, toDebugValue } from '../utils/format';

const initialCreateForm: CreateAccountRequest = {
  accountNo: '',
  customerName: '',
  balance: '0',
  currency: 'VND',
};

const columns: TableColumn<AccountResponse>[] = [
  { key: 'accountNo', header: 'Account No', render: (row) => row.accountNo },
  { key: 'customerName', header: 'Customer', render: (row) => row.customerName },
  { key: 'balance', header: 'Balance', render: (row) => formatCurrency(row.balance, row.currency) },
  {
    key: 'status',
    header: 'Status',
    render: (row) => <StatusBadge status={row.status} tone="success" subtle />,
  },
  { key: 'updatedAt', header: 'Updated', render: (row) => formatDateTime(row.updatedAt) },
];

export function AccountsPage() {
  const [createForm, setCreateForm] = useState(initialCreateForm);
  const [lookupAccountNo, setLookupAccountNo] = useState('');
  const [account, setAccount] = useState<AccountResponse | null>(null);
  const [rawJson, setRawJson] = useState<unknown>({ note: 'Account responses will appear here.' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<unknown>(null);

  async function handleCreateAccount(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await createAccount(createForm);
      setAccount(response.data);
      setRawJson(response);
    } catch (requestError) {
      setError(requestError);
      setRawJson(toDebugValue(requestError));
    } finally {
      setLoading(false);
    }
  }

  async function handleLookupAccount(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await getAccount(lookupAccountNo);
      setAccount(response.data);
      setRawJson(response);
    } catch (requestError) {
      setError(requestError);
      setRawJson(toDebugValue(requestError));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-grid">
      <PageHeader
        title="Accounts"
        description="Create accounts, retrieve them by account number, and keep a polished account summary visible for transfer preparation."
        helper="Existing API integration remains live. When the backend is unavailable, the seeded demo accounts keep the screen useful for layout and data-flow testing."
        actions={<StatusBadge status="Live API preferred" tone="success" subtle />}
      />

      {error ? <ErrorAlert error={error} /> : null}
      {loading ? <LoadingState label="Waiting for account API..." /> : null}

      <div className="two-column-grid">
        <FormSection
          title="Create account"
          description="Sends POST /api/v1/accounts and keeps the raw API response visible for learning."
          badge={<StatusBadge status="Writes live data" tone="warning" subtle />}
        >
          <form className="form-grid" onSubmit={handleCreateAccount}>
            <label>
              Account No
              <input
                value={createForm.accountNo}
                onChange={(event) => setCreateForm((current) => ({ ...current, accountNo: event.target.value }))}
                placeholder="100001"
              />
            </label>
            <label>
              Customer Name
              <input
                value={createForm.customerName}
                onChange={(event) =>
                  setCreateForm((current) => ({ ...current, customerName: event.target.value }))
                }
                placeholder="Nguyen Van A"
              />
            </label>
            <label>
              Balance
              <input
                value={createForm.balance}
                onChange={(event) => setCreateForm((current) => ({ ...current, balance: event.target.value }))}
                placeholder="1000000"
                inputMode="decimal"
              />
            </label>
            <label>
              Currency
              <input
                value={createForm.currency}
                onChange={(event) => setCreateForm((current) => ({ ...current, currency: event.target.value }))}
                placeholder="VND"
              />
            </label>
            <button className="button button--primary" type="submit">
              Create account
            </button>
          </form>
        </FormSection>

        <FormSection
          title="Get account"
          description="Fetches one account by account number to confirm the saved state."
          badge={<StatusBadge status="Reads live data" tone="success" subtle />}
        >
          <form className="form-grid" onSubmit={handleLookupAccount}>
            <label>
              Account No
              <input
                value={lookupAccountNo}
                onChange={(event) => setLookupAccountNo(event.target.value)}
                placeholder="100001"
              />
            </label>
            <button className="button" type="submit">
              Get account
            </button>
          </form>
        </FormSection>
      </div>

      {account ? (
        <section className="result-card card">
          <div className="card-heading">
            <div>
              <h4>Account response</h4>
              <p>Friendly summary card for the latest account payload.</p>
            </div>
            <StatusBadge status="Live response" tone="success" subtle />
          </div>
          <div className="result-grid">
            <div>
              <span>Account No</span>
              <strong>{account.accountNo}</strong>
            </div>
            <div>
              <span>Customer</span>
              <strong>{account.customerName}</strong>
            </div>
            <div>
              <span>Balance</span>
              <strong>{formatCurrency(account.balance, account.currency)}</strong>
            </div>
            <div>
              <span>Status</span>
              <strong>{account.status}</strong>
            </div>
            <div>
              <span>Created</span>
              <strong>{formatDateTime(account.createdAt)}</strong>
            </div>
            <div>
              <span>Updated</span>
              <strong>{formatDateTime(account.updatedAt)}</strong>
            </div>
          </div>
        </section>
      ) : null}

      <DataTable
        caption="Demo account list"
        title="Reference accounts"
        description="Static demo data shown when you need a polished banking view before backend data is ready."
        columns={columns}
        rows={account ? [account] : demoAccounts}
        emptyMessage="No account records available."
        actions={<StatusBadge status={account ? 'Live response' : 'Static demo'} tone={account ? 'success' : 'info'} subtle />}
      />

      <JsonViewer value={rawJson} badgeLabel={account ? 'Live payload' : 'Preview payload'} />
    </div>
  );
}
