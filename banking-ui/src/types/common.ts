export type ApiResponse<T> = {
  success: boolean;
  data: T;
};

export type ApiErrorResponse = {
  success: false;
  code: string;
  message: string;
};

export type UnsupportedApiResult = {
  supported: false;
  resource: string;
  service: string;
  reason: string;
  suggestedEndpoint: string;
};

export type ServiceStatus = {
  name: string;
  state: 'healthy' | 'reachable' | 'offline';
  detail: string;
  httpStatus?: number;
  payload?: unknown;
};
