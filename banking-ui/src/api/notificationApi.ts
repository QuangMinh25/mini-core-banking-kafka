import type { UnsupportedApiResult } from '../types/common';

export async function getNotificationLogs(): Promise<UnsupportedApiResult> {
  return {
    supported: false,
    resource: 'Notification logs',
    service: 'notification-service',
    reason:
      'The notification service has Kafka consumers and JPA entities, but no checked-in HTTP controller exposes notification log records.',
    suggestedEndpoint: 'GET /api/v1/notifications',
  };
}
