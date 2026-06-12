import type { ReactNode } from 'react';

type ActionCardProps = {
  title: string;
  description: string;
  icon?: ReactNode;
  action?: ReactNode;
  meta?: ReactNode;
};

export function ActionCard({ title, description, icon, action, meta }: ActionCardProps) {
  return (
    <section className="action-card card">
      <div className="action-card__header">
        <div className="action-card__icon">{icon}</div>
        {meta ? <div>{meta}</div> : null}
      </div>
      <div className="action-card__body">
        <h4>{title}</h4>
        <p>{description}</p>
      </div>
      {action ? <div className="action-card__footer">{action}</div> : null}
    </section>
  );
}
