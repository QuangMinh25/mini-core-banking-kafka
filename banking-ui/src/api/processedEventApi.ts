import type { UnsupportedApiResult } from '../types/common';

export async function getProcessedEvents(): Promise<UnsupportedApiResult> {
  return {
    supported: false,
    resource: 'Processed events',
    service: 'notification-service',
    reason:
      'The repo proves duplicate protection at the database and consumer layer, but there is no checked-in HTTP controller for processed event inspection.',
    suggestedEndpoint: 'GET /api/v1/processed-events',
  };
}
