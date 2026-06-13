import type { ApiResponse } from '../types/common';
import type { StatementQuery, StatementResponse } from '../types/ledger';
import { requestJson } from './httpClient';

export function getStatement(query: StatementQuery) {
  const searchParams = new URLSearchParams({
    page: String(query.page),
    size: String(query.size),
  });

  if (query.fromDate) {
    searchParams.set('fromDate', query.fromDate);
  }

  if (query.toDate) {
    searchParams.set('toDate', query.toDate);
  }

  return requestJson<ApiResponse<StatementResponse>>(`/api/v1/accounts/${encodeURIComponent(query.accountNo)}/statement?${searchParams.toString()}`);
}
