import { BellRing } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getNotificationLogs } from '../api/notificationApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoNotifications } from '../data/demoData';
import type { UnsupportedApiResult } from '../types/common';
import type { NotificationLog } from '../types/notification';
import { formatCurrency, formatDateTime } from '../utils/format';

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
  const [result, setResult] = useState<UnsupportedApiResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function load() {
      const response = await getNotificationLogs();
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
        title="Notifications"
        description="Consumer-side visibility for the records created after Kafka events are handled."
        helper="When a transfer event is consumed successfully, notification logs should explain what message was created and when."
        actions={<StatusBadge status="Static demo fallback" tone="info" subtle />}
      />
      {loading ? <LoadingState label="Checking notification API availability..." /> : null}
      {result ? (
        <>
          <DataTable
            caption="Notification logs table"
            title="Notification log stream"
            description="Static demo rows show the type of consumer-side visibility the dashboard expects."
            columns={columns}
            rows={demoNotifications}
            emptyMessage="No notification logs to show."
            actions={<StatusBadge status="Static demo" tone="info" subtle />}
          />
          <section className="note-banner card">
            <div className="note-banner__icon">
              <BellRing size={18} />
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
