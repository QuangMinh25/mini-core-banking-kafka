import { useState } from 'react';
import { getStatement } from '../api/statementApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { ErrorAlert } from '../components/ErrorAlert';
import { FormSection } from '../components/FormSection';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoStatement } from '../data/demoData';
import type { LedgerEntry, StatementQuery, StatementResponse } from '../types/ledger';
import { formatCurrency, formatDateTime, getErrorMessage } from '../utils/format';

const initialQuery: StatementQuery = {
  accountNo: '',
  fromDate: '',
  toDate: '',
  page: 0,
  size: 20,
};

const columns: TableColumn<LedgerEntry>[] = [
  { key: 'transactionReferenceNo', header: 'Transaction Ref', render: (row) => row.transactionReferenceNo },
  {
    key: 'entryType',
    header: 'Entry Type',
    render: (row) => (
      <StatusBadge status={row.entryType} tone={row.entryType === 'CREDIT' ? 'success' : 'warning'} subtle />
    ),
  },
  { key: 'amount', header: 'Amount', render: (row) => formatCurrency(row.amount) },
  { key: 'balanceAfter', header: 'Balance After', render: (row) => formatCurrency(row.balanceAfter) },
  { key: 'description', header: 'Description', render: (row) => row.description },
  { key: 'createdAt', header: 'Created At', render: (row) => formatDateTime(row.createdAt) },
];

export function StatementPage() {
  const [query, setQuery] = useState(initialQuery);
  const [statement, setStatement] = useState<StatementResponse | null>(null);
  const [rawJson, setRawJson] = useState<unknown>({ note: 'Statement result will appear here.' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await getStatement(query);
      setStatement(response.data);
      setRawJson(response);
    } catch (requestError) {
      setError(getErrorMessage(requestError));
      setRawJson(requestError);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-grid">
      <PageHeader
        title="Statement"
        description="Query ledger-backed account history by account number and optional date range, with a demo ledger ready when the API is not."
        helper="Ledger entries show how each transfer affected the account. DEBIT and CREDIT rows are easier to reason about than only looking at the transfer summary."
        actions={<StatusBadge status="Ledger view" tone="info" subtle />}
      />

      {error ? <ErrorAlert message={error} /> : null}
      {loading ? <LoadingState label="Loading statement..." /> : null}

      <FormSection
        title="Query statement"
        description="Uses GET /api/v1/accounts/{accountNo}/statement with paging and optional date filters."
        badge={<StatusBadge status="Live query" tone="success" subtle />}
      >
        <form className="form-grid" onSubmit={handleSubmit}>
          <label>
            Account No
            <input
              value={query.accountNo}
              onChange={(event) => setQuery((current) => ({ ...current, accountNo: event.target.value }))}
              placeholder="100001"
            />
          </label>
          <label>
            From Date
            <input
              type="date"
              value={query.fromDate}
              onChange={(event) => setQuery((current) => ({ ...current, fromDate: event.target.value }))}
            />
          </label>
          <label>
            To Date
            <input
              type="date"
              value={query.toDate}
              onChange={(event) => setQuery((current) => ({ ...current, toDate: event.target.value }))}
            />
          </label>
          <label>
            Page
            <input
              type="number"
              value={query.page}
              onChange={(event) =>
                setQuery((current) => ({ ...current, page: Number(event.target.value) || 0 }))
              }
            />
          </label>
          <label>
            Size
            <input
              type="number"
              value={query.size}
              onChange={(event) =>
                setQuery((current) => ({ ...current, size: Number(event.target.value) || 20 }))
              }
            />
          </label>
          <button className="button button--primary" type="submit">
            Load statement
          </button>
        </form>
      </FormSection>

      <DataTable
        caption="Statement ledger entries"
        title="Ledger entries"
        description={
          statement
            ? `Page ${statement.page + 1} of ${Math.max(statement.totalPages, 1)} | ${statement.totalElements} total entries`
            : 'Static demo rows are shown until a live statement response arrives.'
        }
        columns={columns}
        rows={statement?.entries ?? demoStatement.entries}
        emptyMessage="No ledger entries to show."
        actions={
          <StatusBadge status={statement ? 'Live response' : 'Static demo'} tone={statement ? 'success' : 'info'} subtle />
        }
      />

      <JsonViewer value={rawJson} badgeLabel={statement ? 'Live payload' : 'Preview payload'} />
    </div>
  );
}
