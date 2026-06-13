import type { ApiResponse, NotificationPageResponse } from '../types/common';
import type { ProcessedEvent } from '../types/processedEvent';
import { requestJson } from './httpClient';

export function getProcessedEvents(page = 0, size = 20) {
  return requestJson<ApiResponse<NotificationPageResponse<ProcessedEvent>>>(
    `/api/v1/processed-events?page=${page}&size=${size}`,
  );
}
