export type ApiResponse<T> = {
  success: boolean;
  data: T;
};

export type ApiErrorResponse = {
  success: false;
  code: string;
  message: string;
  correlationId?: string;
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
  url?: string;
  httpStatus?: number;
  payload?: unknown;
};

export type CorePageResponse<T> = {
  items: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

export type NotificationPageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};
