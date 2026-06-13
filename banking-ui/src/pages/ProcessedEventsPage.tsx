import { ShieldCheck } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getProcessedEvents } from '../api/processedEventApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoProcessedEvents } from '../data/demoData';
import type { ProcessedEvent } from '../types/processedEvent';
import { formatDateTime, getErrorMessage, toDebugValue } from '../utils/format';

const columns: TableColumn<ProcessedEvent>[] = [
  { key: 'eventId', header: 'Event ID', render: (row) => row.eventId },
  { key: 'topic', header: 'Topic', render: (row) => row.topic },
  { key: 'processedAt', header: 'Processed At', render: (row) => formatDateTime(row.processedAt) },
];

export function ProcessedEventsPage() {
  const [rows, setRows] = useState<ProcessedEvent[]>(demoProcessedEvents);
  const [isLive, setIsLive] = useState(false);
  const [note, setNote] = useState('Static demo rows illustrate the deduplication ledger used by the consumer.');
  const [rawJson, setRawJson] = useState<unknown>({ note: 'Processed event responses will appear here.' });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function load() {
      try {
        const response = await getProcessedEvents();
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
          setRows(demoProcessedEvents);
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
        title="Processed Events"
        description="Visibility for the event IDs already handled by notification-service."
        helper="processed_events prevents duplicate Kafka event processing. If the same event is delivered again, the consumer can skip creating a duplicate business-side record."
        actions={<StatusBadge status={isLive ? 'Gateway live' : 'Duplicate protection'} tone={isLive ? 'success' : 'info'} subtle />}
      />
      {loading ? <LoadingState label="Checking processed events API availability..." /> : null}
      {!loading ? (
        <>
          <DataTable
            caption="Processed event table"
            title="Processed event registry"
            description={note}
            columns={columns}
            rows={rows}
            emptyMessage="No processed events to show."
            actions={<StatusBadge status={isLive ? 'Live response' : 'Static demo'} tone={isLive ? 'success' : 'info'} subtle />}
          />
          <section className="note-banner card">
            <div className="note-banner__icon">
              <ShieldCheck size={18} />
            </div>
            <div>
              <strong>{isLive ? 'Why this matters' : 'Static fallback active'}</strong>
              <p>
                The consumer uses processed event tracking to skip duplicate deliveries and avoid replaying the same business outcome twice.
              </p>
              <small>
                {isLive
                  ? 'Live data is flowing through the gateway route for processed events.'
                  : 'The gateway route for `GET /api/v1/processed-events` was unavailable, so demo rows stayed on screen.'}
              </small>
            </div>
          </section>
          <JsonViewer value={rawJson} badgeLabel={isLive ? 'Live payload' : 'Fallback debug'} />
        </>
      ) : null}
    </div>
  );
}
