import { ShieldCheck } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getProcessedEvents } from '../api/processedEventApi';
import { DataTable, type TableColumn } from '../components/DataTable';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatusBadge } from '../components/StatusBadge';
import { demoProcessedEvents } from '../data/demoData';
import type { UnsupportedApiResult } from '../types/common';
import type { ProcessedEvent } from '../types/processedEvent';
import { formatDateTime } from '../utils/format';

const columns: TableColumn<ProcessedEvent>[] = [
  { key: 'eventId', header: 'Event ID', render: (row) => row.eventId },
  { key: 'topic', header: 'Topic', render: (row) => row.topic },
  { key: 'processedAt', header: 'Processed At', render: (row) => formatDateTime(row.processedAt) },
];

export function ProcessedEventsPage() {
  const [result, setResult] = useState<UnsupportedApiResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function load() {
      const response = await getProcessedEvents();
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
        title="Processed Events"
        description="Visibility for the event IDs already handled by notification-service."
        helper="processed_events prevents duplicate Kafka event processing. If the same event is delivered again, the consumer can skip creating a duplicate business-side record."
        actions={<StatusBadge status="Duplicate protection" tone="success" subtle />}
      />
      {loading ? <LoadingState label="Checking processed events API availability..." /> : null}
      {result ? (
        <>
          <DataTable
            caption="Processed event table"
            title="Processed event registry"
            description="Static demo rows illustrate the deduplication ledger used by the consumer."
            columns={columns}
            rows={demoProcessedEvents}
            emptyMessage="No processed events to show."
            actions={<StatusBadge status="Static demo" tone="info" subtle />}
          />
          <section className="note-banner card">
            <div className="note-banner__icon">
              <ShieldCheck size={18} />
            </div>
            <div>
              <strong>Why this matters</strong>
              <p>The consumer uses processed event tracking to skip duplicate deliveries and avoid replaying the same business outcome twice.</p>
              <small>{result.reason}</small>
            </div>
          </section>
          <JsonViewer value={result} />
        </>
      ) : null}
    </div>
  );
}
