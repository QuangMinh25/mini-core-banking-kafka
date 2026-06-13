import type { ApiResponse, CorePageResponse } from '../types/common';
import type { OutboxEvent } from '../types/outbox';
import { requestJson } from './httpClient';

export function getOutboxEvents(page = 0, size = 20) {
  return requestJson<ApiResponse<CorePageResponse<OutboxEvent>>>(
    `/api/v1/outbox-events?page=${page}&size=${size}`,
  );
}
