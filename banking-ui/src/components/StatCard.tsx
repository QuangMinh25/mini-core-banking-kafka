import type { ReactNode } from 'react';
import { StatusBadge } from './StatusBadge';

type StatCardProps = {
  label: string;
  value: string;
  tone?: 'default' | 'success' | 'warning' | 'danger';
  meta?: string;
  icon?: ReactNode;
  badge?: string;
};

export function StatCard({
  label,
  value,
  tone = 'default',
  meta,
  icon,
  badge,
}: StatCardProps) {
  const badgeTone =
    tone === 'success' ? 'success' : tone === 'warning' ? 'warning' : tone === 'danger' ? 'danger' : 'info';

  return (
    <article className={`stat-card stat-card--${tone}`}>
      <div className="stat-card__label-row">
        <p>{label}</p>
        <div className="stat-card__meta">
          {badge ? <StatusBadge status={badge} tone={badgeTone} subtle /> : null}
          {icon ? <span className="stat-card__icon">{icon}</span> : null}
        </div>
      </div>
      <h4>{value}</h4>
      {meta ? <small>{meta}</small> : null}
    </article>
  );
}
