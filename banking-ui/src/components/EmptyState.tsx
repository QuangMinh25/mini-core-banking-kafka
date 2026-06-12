import type { ReactNode } from 'react';

type EmptyStateProps = {
  title: string;
  description: string;
  note?: string;
  action?: ReactNode;
  icon?: ReactNode;
};

export function EmptyState({ title, description, note, action, icon }: EmptyStateProps) {
  return (
    <section className="empty-state card">
      {icon ? <div className="empty-state__icon">{icon}</div> : null}
      <h4>{title}</h4>
      <p>{description}</p>
      {note ? <small>{note}</small> : null}
      {action ? <div className="empty-state__action">{action}</div> : null}
    </section>
  );
}
