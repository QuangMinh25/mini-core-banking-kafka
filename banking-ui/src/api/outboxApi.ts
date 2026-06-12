import type { UnsupportedApiResult } from '../types/common';

export async function getOutboxEvents(): Promise<UnsupportedApiResult> {
  return {
    supported: false,
    resource: 'Outbox events',
    service: 'banking-core-service',
    reason:
      'The repo contains outbox entities and a publisher job, but no checked-in HTTP controller exposes outbox event rows.',
    suggestedEndpoint: 'GET /api/v1/outbox-events',
  };
}
