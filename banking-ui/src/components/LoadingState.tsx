type LoadingStateProps = {
  label?: string;
};

export function LoadingState({ label = 'Loading data...' }: LoadingStateProps) {
  return (
    <div className="loading-state card">
      <div className="loading-state__pulse" />
      <div>
        <strong>Loading</strong>
        <p>{label}</p>
      </div>
    </div>
  );
}
