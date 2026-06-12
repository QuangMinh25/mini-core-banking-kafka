type StatusBadgeProps = {
  status: string;
  tone?: 'neutral' | 'success' | 'warning' | 'danger' | 'info';
  subtle?: boolean;
};

const toneClassMap = {
  neutral: 'status-badge--neutral',
  success: 'status-badge--success',
  warning: 'status-badge--warning',
  danger: 'status-badge--danger',
  info: 'status-badge--info',
} as const;

export function StatusBadge({ status, tone = 'neutral', subtle = false }: StatusBadgeProps) {
  return (
    <span className={`status-badge ${toneClassMap[tone]}${subtle ? ' status-badge--subtle' : ''}`}>
      {status}
    </span>
  );
}
