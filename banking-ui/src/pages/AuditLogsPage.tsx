import { ClipboardList } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getAuditLogs } from '../api/auditApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoAuditLogs } from '../data/demoData';
import type { AuditLog } from '../types/audit';
import { formatDateTime, getErrorMessage, toDebugValue } from '../utils/format';

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
  const [rows, setRows] = useState<AuditLog[]>(demoAuditLogs);
  const [isLive, setIsLive] = useState(false);
  const [note, setNote] = useState('Static demo rows show how action-level tracing can look before live gateway data is available.');
  const [rawJson, setRawJson] = useState<unknown>({ note: 'Audit log responses will appear here.' });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function load() {
      try {
        const response = await getAuditLogs();
        if (mounted) {
          setRows(response.data.items);
          setIsLive(true);
          setNote(
            `Live gateway response | Page ${response.data.page + 1} of ${Math.max(response.data.totalPages, 1)} | ${response.data.totalElements} total rows`,
          );
          setRawJson(response);
        }
      } catch (requestError) {
        if (mounted) {
          setRows(demoAuditLogs);
          setIsLive(false);
          setNote(`Static demo fallback active because ${getErrorMessage(requestError)}.`);
          setRawJson(toDebugValue(requestError));
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
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
        helper="Audit records are useful for tracing business actions and failures. When the live gateway route is unavailable, the page keeps a polished static trail visible."
        actions={<StatusBadge status={isLive ? 'Gateway live' : 'Static timeline'} tone={isLive ? 'success' : 'info'} subtle />}
      />
      {loading ? <LoadingState label="Checking audit log API availability..." /> : null}
      {!loading ? (
        <>
          <DataTable
            caption="Audit log table"
            title="Audit trail"
            description={note}
            columns={columns}
            rows={rows}
            emptyMessage="No audit logs to show."
            actions={<StatusBadge status={isLive ? 'Live response' : 'Static demo'} tone={isLive ? 'success' : 'info'} subtle />}
          />
          {!isLive ? (
            <section className="note-banner card">
              <div className="note-banner__icon">
                <ClipboardList size={18} />
              </div>
              <div>
                <strong>Static fallback active</strong>
                <p>The gateway route for `GET /api/v1/audit-logs` was unavailable, so the page stayed usable with demo audit history.</p>
                <small>Live audit rows will appear automatically when the gateway can reach `banking-core-service`.</small>
              </div>
            </section>
          ) : null}
          <JsonViewer value={rawJson} badgeLabel={isLive ? 'Live payload' : 'Fallback debug'} />
        </>
      ) : null}
    </div>
  );
}
