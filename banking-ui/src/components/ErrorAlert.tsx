import { getErrorDetails } from '../utils/format';

type ErrorAlertProps = {
  error: unknown;
  title?: string;
};

export function ErrorAlert({ error, title = 'Live API unavailable' }: ErrorAlertProps) {
  const details = getErrorDetails(error);

  return (
    <div className="error-alert" role="alert">
      <strong>{title}</strong>
      <p>{details.message}</p>
      {details.code || details.correlationId ? (
        <div className="error-alert__meta">
          {details.code ? <span>Code: {details.code}</span> : null}
          {details.correlationId ? <span>Correlation ID: {details.correlationId}</span> : null}
        </div>
      ) : null}
    </div>
  );
}
