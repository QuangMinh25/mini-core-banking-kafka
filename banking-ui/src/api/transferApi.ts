import type { ApiResponse } from '../types/common';
import type { TransferRequest, TransferResponse } from '../types/transfer';
import { requestJson } from './httpClient';

export function sendTransfer(request: TransferRequest, idempotencyKey: string) {
  return requestJson<ApiResponse<TransferResponse>>('core', '/api/v1/transfers', {
    method: 'POST',
    headers: {
      'Idempotency-Key': idempotencyKey,
    },
    body: JSON.stringify(request),
  });
}
