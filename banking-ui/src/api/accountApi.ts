import type { AccountResponse, CreateAccountRequest } from '../types/account';
import type { ApiResponse } from '../types/common';
import { requestJson } from './httpClient';

export function createAccount(request: CreateAccountRequest) {
  return requestJson<ApiResponse<AccountResponse>>('core', '/api/v1/accounts', {
    method: 'POST',
    body: JSON.stringify({
      ...request,
      balance: request.balance === '' ? null : request.balance,
    }),
  });
}

export function getAccount(accountNo: string) {
  return requestJson<ApiResponse<AccountResponse>>(
    'core',
    `/api/v1/accounts/${encodeURIComponent(accountNo)}`,
  );
}
