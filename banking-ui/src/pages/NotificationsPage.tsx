import { BellRing } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getNotificationLogs } from '../api/notificationApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoNotifications } from '../data/demoData';
import type { NotificationLog } from '../types/notification';
import { formatCurrency, formatDateTime, getErrorMessage, toDebugValue } from '../utils/format';

const columns: TableColumn<NotificationLog>[] = [
  { key: 'eventId', header: 'Event ID', render: (row) => row.eventId },
  { key: 'referenceNo', header: 'Reference', render: (row) => row.referenceNo },
  { key: 'message', header: 'Message', render: (row) => row.message },
  {
    key: 'status',
    header: 'Status',
    render: (row) => (
      <StatusBadge status={row.status} tone={row.status === 'DELIVERED' ? 'success' : 'warning'} subtle />
    ),
  },
  { key: 'amount', header: 'Amount', render: (row) => formatCurrency(row.amount, row.currency) },
  { key: 'createdAt', header: 'Created At', render: (row) => formatDateTime(row.createdAt) },
];

export function NotificationsPage() {
  const [rows, setRows] = useState<NotificationLog[]>(demoNotifications);
  const [isLive, setIsLive] = useState(false);
  const [note, setNote] = useState('Static demo rows show the type of consumer-side visibility the dashboard expects.');
  const [rawJson, setRawJson] = useState<unknown>({ note: 'Notification responses will appear here.' });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function load() {
      try {
        const response = await getNotificationLogs();
        if (mounted) {
          setRows(response.data.content);
          setIsLive(true);
          setNote(
            `Live gateway response | Page ${response.data.page + 1} of ${Math.max(response.data.totalPages, 1)} | ${response.data.totalElements} total rows`,
          );
          setRawJson(response);
        }
      } catch (requestError) {
        if (mounted) {
          setRows(demoNotifications);
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
        title="Notifications"
        description="Consumer-side visibility for the records created after Kafka events are handled."
        helper="When a transfer event is consumed successfully, notification logs should explain what message was created and when."
        actions={<StatusBadge status={isLive ? 'Gateway live' : 'Static demo fallback'} tone={isLive ? 'success' : 'info'} subtle />}
      />
      {loading ? <LoadingState label="Checking notification API availability..." /> : null}
      {!loading ? (
        <>
          <DataTable
            caption="Notification logs table"
            title="Notification log stream"
            description={note}
            columns={columns}
            rows={rows}
            emptyMessage="No notification logs to show."
            actions={<StatusBadge status={isLive ? 'Live response' : 'Static demo'} tone={isLive ? 'success' : 'info'} subtle />}
          />
          {!isLive ? (
            <section className="note-banner card">
              <div className="note-banner__icon">
                <BellRing size={18} />
              </div>
              <div>
                <strong>Static fallback active</strong>
                <p>The gateway route for `GET /api/v1/notifications` was unavailable, so the page kept demo rows instead of crashing.</p>
                <small>Start `notification-service` and route it through `api-gateway-service` to see live data.</small>
              </div>
            </section>
          ) : null}
          <JsonViewer value={rawJson} badgeLabel={isLive ? 'Live payload' : 'Fallback debug'} />
        </>
      ) : null}
    </div>
  );
}
