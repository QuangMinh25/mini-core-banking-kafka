import type { ApiResponse, NotificationPageResponse } from '../types/common';
import type { NotificationLog } from '../types/notification';
import { requestJson } from './httpClient';

export function getNotificationLogs(page = 0, size = 20) {
  return requestJson<ApiResponse<NotificationPageResponse<NotificationLog>>>(
    `/api/v1/notifications?page=${page}&size=${size}`,
  );
}
