import { ClipboardList } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getAuditLogs } from '../api/auditApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoAuditLogs } from '../data/demoData';
import type { UnsupportedApiResult } from '../types/common';
import type { AuditLog } from '../types/audit';
import { formatDateTime } from '../utils/format';

const columns: TableColumn<AuditLog>[] = [
  { key: 'action', header: 'Action', render: (row) => row.action },
  { key: 'resourceType', header: 'Resource Type', render: (row) => row.resourceType },
  { key: 'resourceId', header: 'Resource ID', render: (row) => row.resourceId },
  {
    key: 'status',
    header: 'Status',
    render: (row) => (
      <StatusBadge
        status={row.status}
        tone={row.status === 'SUCCESS' ? 'success' : row.status === 'RETRYING' ? 'warning' : 'danger'}
        subtle
      />
    ),
  },
  { key: 'errorMessage', header: 'Error', render: (row) => row.errorMessage ?? '-' },
  { key: 'createdAt', header: 'Created At', render: (row) => formatDateTime(row.createdAt) },
];

export function AuditLogsPage() {
  const [result, setResult] = useState<UnsupportedApiResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function load() {
      const response = await getAuditLogs();
      if (mounted) {
        setResult(response);
        setLoading(false);
      }
    }

    void load();
    return () => {
      mounted = false;
    };
  }, []);

  return (
    <div className="page-grid">
      <PageHeader
        title="Audit Logs"
        description="Visibility for action-level history inside banking-core-service."
        helper="Audit records are useful for tracing business actions and failures, but the current repo does not expose them through HTTP yet."
        actions={<StatusBadge status="Static timeline" tone="info" subtle />}
      />
      {loading ? <LoadingState label="Checking audit log API availability..." /> : null}
      {result ? (
        <>
          <DataTable
            caption="Audit log table"
            title="Audit trail"
            description="Static demo rows show how action-level tracing can look before the backend exposes an audit endpoint."
            columns={columns}
            rows={demoAuditLogs}
            emptyMessage="No audit logs to show."
            actions={<StatusBadge status="Static demo" tone="info" subtle />}
          />
          <section className="note-banner card">
            <div className="note-banner__icon">
              <ClipboardList size={18} />
            </div>
            <div>
              <strong>Live endpoint not available</strong>
              <p>{result.reason}</p>
              <small>Suggested backend endpoint: {result.suggestedEndpoint}</small>
            </div>
          </section>
          <JsonViewer value={result} />
        </>
      ) : null}
    </div>
  );
}
