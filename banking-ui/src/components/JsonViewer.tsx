import { StatusBadge } from './StatusBadge';

type JsonViewerProps = {
  title?: string;
  value: unknown;
  badgeLabel?: string;
};

export function JsonViewer({
  title = 'Raw JSON',
  value,
  badgeLabel = 'Backend trace',
}: JsonViewerProps) {
  return (
    <section className="json-viewer card">
      <div className="card-heading">
        <div>
          <h4>{title}</h4>
          <p>Keep the transport payload visible while polishing the UI around it.</p>
        </div>
        <StatusBadge status={badgeLabel} tone="neutral" subtle />
      </div>
      <pre>{JSON.stringify(value, null, 2)}</pre>
    </section>
  );
}
