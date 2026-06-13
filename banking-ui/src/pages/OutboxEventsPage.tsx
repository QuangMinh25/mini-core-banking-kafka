import { DatabaseZap } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getOutboxEvents } from '../api/outboxApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoOutboxEvents } from '../data/demoData';
import type { OutboxEvent } from '../types/outbox';
import { formatDateTime, getErrorMessage, toDebugValue } from '../utils/format';

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
  const [rows, setRows] = useState<OutboxEvent[]>(demoOutboxEvents);
  const [isLive, setIsLive] = useState(false);
  const [note, setNote] = useState('Static demo rows mirror the shape expected from the gateway-routed outbox endpoint.');
  const [rawJson, setRawJson] = useState<unknown>({ note: 'Outbox event responses will appear here.' });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function load() {
      try {
        const response = await getOutboxEvents();
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
          setRows(demoOutboxEvents);
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
        title="Outbox Events"
        description="Use this page to understand whether transfer events are visible through an HTTP endpoint and how publish state should look."
        helper="Outbox status matters because it helps explain whether an event is still waiting, published successfully, or stuck after a failure."
        actions={<StatusBadge status={isLive ? 'Gateway live' : 'Static demo fallback'} tone={isLive ? 'success' : 'info'} subtle />}
      />
      {loading ? <LoadingState label="Checking outbox API availability..." /> : null}
      {!loading ? (
        <>
          <DataTable
            caption="Outbox event table"
            title="Outbox event queue"
            description={note}
            columns={columns}
            rows={rows}
            emptyMessage="No outbox events to show."
            actions={<StatusBadge status={isLive ? 'Live response' : 'Static demo'} tone={isLive ? 'success' : 'info'} subtle />}
          />
          {!isLive ? (
            <section className="note-banner card">
              <div className="note-banner__icon">
                <DatabaseZap size={18} />
              </div>
              <div>
                <strong>Static fallback active</strong>
                <p>The gateway route for `GET /api/v1/outbox-events` was unavailable, so the page kept polished demo rows.</p>
                <small>Start `banking-core-service` behind the gateway to inspect live outbox publication state.</small>
              </div>
            </section>
          ) : null}
          <JsonViewer value={rawJson} badgeLabel={isLive ? 'Live payload' : 'Fallback debug'} />
        </>
      ) : null}
    </div>
  );
}
