import type { UnsupportedApiResult } from '../types/common';

export async function getAuditLogs(): Promise<UnsupportedApiResult> {
  return {
    supported: false,
    resource: 'Audit logs',
    service: 'banking-core-service',
    reason:
      'Audit persistence code exists in banking-core-service, but the current repo does not expose audit logs through an HTTP controller.',
    suggestedEndpoint: 'GET /api/v1/audit-logs',
  };
}
