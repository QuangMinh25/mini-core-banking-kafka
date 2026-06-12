import type { ReactNode } from 'react';

type FormSectionProps = {
  title: string;
  description: string;
  children: ReactNode;
  badge?: ReactNode;
};

export function FormSection({ title, description, children, badge }: FormSectionProps) {
  return (
    <section className="form-section card">
      <div className="card-heading">
        <div>
          <h4>{title}</h4>
          <p>{description}</p>
        </div>
        {badge ? <div>{badge}</div> : null}
      </div>
      {children}
    </section>
  );
}
