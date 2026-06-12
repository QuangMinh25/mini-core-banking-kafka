import { BellDot, Search, Server, ShieldCheck } from 'lucide-react';
import { StatusBadge } from '../components/StatusBadge';

type TopbarProps = {
  title: string;
  description: string;
};

export function Topbar({ title, description }: TopbarProps) {
  const today = new Intl.DateTimeFormat('en-GB', {
    weekday: 'long',
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date());

  return (
    <header className="topbar">
      <div className="topbar__title">
        <p className="topbar__kicker">Internet banking dashboard</p>
        <h2>{title}</h2>
        <p className="topbar__description">{description}</p>
      </div>
      <div className="topbar__actions">
        <div className="topbar__search">
          <Search size={16} />
          <span>Search flows, events, accounts</span>
        </div>
        <div className="topbar__meta">
          <StatusBadge status="Local dev" tone="neutral" subtle />
          <StatusBadge status="Kafka learning mode" tone="info" subtle />
        </div>
        <div className="topbar__panel">
          <div>
            <small>{today}</small>
            <strong>Operations desk</strong>
          </div>
          <div className="topbar__panel-icons">
            <span className="badge badge--neutral">
              <Server size={14} />
              API
            </span>
            <span className="badge">
              <BellDot size={14} />
              Events
            </span>
            <span className="badge badge--success">
              <ShieldCheck size={14} />
              Safe demo
            </span>
          </div>
        </div>
      </div>
    </header>
  );
}
