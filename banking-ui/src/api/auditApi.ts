import type { ApiResponse, CorePageResponse } from '../types/common';
import type { AuditLog } from '../types/audit';
import { requestJson } from './httpClient';

export function getAuditLogs(page = 0, size = 20) {
  return requestJson<ApiResponse<CorePageResponse<AuditLog>>>(`/api/v1/audit-logs?page=${page}&size=${size}`);
}
