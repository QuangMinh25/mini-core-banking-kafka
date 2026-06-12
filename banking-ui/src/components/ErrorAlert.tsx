type ErrorAlertProps = {
  message: string;
  title?: string;
};

export function ErrorAlert({ message, title = 'Live API unavailable' }: ErrorAlertProps) {
  return (
    <div className="error-alert" role="alert">
      <strong>{title}</strong>
      <p>{message}</p>
    </div>
  );
}
