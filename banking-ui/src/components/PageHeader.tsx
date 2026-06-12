import type { ReactNode } from 'react';

type PageHeaderProps = {
  title: string;
  description: string;
  helper?: string;
  actions?: ReactNode;
  eyebrow?: string;
};

export function PageHeader({
  title,
  description,
  helper,
  actions,
  eyebrow = 'Operations workspace',
}: PageHeaderProps) {
  return (
    <section className="page-header card">
      <div>
        <p className="section-label">{eyebrow}</p>
        <h3>{title}</h3>
        <p>{description}</p>
        {helper ? <p className="page-header__helper">{helper}</p> : null}
      </div>
      {actions ? <div className="page-header__actions">{actions}</div> : null}
    </section>
  );
}
