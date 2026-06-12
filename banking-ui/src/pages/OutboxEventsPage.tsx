import { DatabaseZap } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getOutboxEvents } from '../api/outboxApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoOutboxEvents } from '../data/demoData';
import type { UnsupportedApiResult } from '../types/common';
import type { OutboxEvent } from '../types/outbox';
import { formatDateTime } from '../utils/format';

const columns: TableColumn<OutboxEvent>[] = [
  { key: 'eventId', header: 'Event ID', render: (row) => row.eventId },
  { key: 'topic', header: 'Topic', render: (row) => row.topic },
  { key: 'eventType', header: 'Event Type', render: (row) => row.eventType },
  {
    key: 'status',
    header: 'Status',
    render: (row) => (
      <StatusBadge
        status={row.status}
        tone={row.status === 'PUBLISHED' ? 'success' : row.status === 'RETRYING' ? 'warning' : 'info'}
        subtle
      />
    ),
  },
  { key: 'retryCount', header: 'Retry Count', render: (row) => String(row.retryCount) },
  { key: 'createdAt', header: 'Created At', render: (row) => formatDateTime(row.createdAt) },
  { key: 'publishedAt', header: 'Published At', render: (row) => formatDateTime(row.publishedAt) },
];

export function OutboxEventsPage() {
  const [result, setResult] = useState<UnsupportedApiResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function load() {
      const response = await getOutboxEvents();
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
        title="Outbox Events"
        description="Use this page to understand whether transfer events are visible through an HTTP endpoint and how publish state should look."
        helper="Outbox status matters because it helps explain whether an event is still waiting, published successfully, or stuck after a failure."
        actions={<StatusBadge status="Static demo fallback" tone="info" subtle />}
      />
      {loading ? <LoadingState label="Checking outbox API availability..." /> : null}
      {result ? (
        <>
          <DataTable
            caption="Outbox event table"
            title="Outbox event queue"
            description="Static demo rows mirror the shape expected from a future outbox inspection endpoint."
            columns={columns}
            rows={demoOutboxEvents}
            emptyMessage="No outbox events to show."
            actions={<StatusBadge status="Static demo" tone="info" subtle />}
          />
          <section className="note-banner card">
            <div className="note-banner__icon">
              <DatabaseZap size={18} />
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
