import { buildApiUrl } from './apiConfig';
import type { ApiErrorResponse, ServiceStatus } from '../types/common';

export class HttpError extends Error {
  status: number;
  payload: unknown;
  code?: string;
  correlationId?: string;
  requestCorrelationId?: string;
  url?: string;

  constructor(
    message: string,
    status: number,
    payload: unknown,
    options?: {
      code?: string;
      correlationId?: string;
      requestCorrelationId?: string;
      url?: string;
    },
  ) {
    super(message);
    this.name = 'HttpError';
    this.status = status;
    this.payload = payload;
    this.code = options?.code;
    this.correlationId = options?.correlationId;
    this.requestCorrelationId = options?.requestCorrelationId;
    this.url = options?.url;
  }

  toJSON() {
    return {
      name: this.name,
      message: this.message,
      status: this.status,
      code: this.code,
      correlationId: this.correlationId,
      requestCorrelationId: this.requestCorrelationId,
      url: this.url,
      payload: this.payload,
    };
  }
}

function createCorrelationId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID();
  }

  return `cid-${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function buildHeaders(initHeaders: RequestInit['headers'], correlationId: string) {
  const headers = new Headers(initHeaders);
  headers.set('Accept', 'application/json');

  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  headers.set('X-Correlation-Id', correlationId);
  return headers;
}

function extractErrorDetails(payload: unknown, response: Response, requestCorrelationId: string) {
  const apiPayload = payload && typeof payload === 'object' ? (payload as ApiErrorResponse) : undefined;
  const responseCorrelationId =
    response.headers.get('X-Correlation-Id') ??
    response.headers.get('x-correlation-id') ??
    apiPayload?.correlationId;

  return {
    message: extractErrorMessage(payload, response.status),
    code: apiPayload?.code,
    correlationId: responseCorrelationId,
    requestCorrelationId,
    url: response.url,
  };
}

export async function requestJson<T>(path: string, init?: RequestInit): Promise<T> {
  const correlationId = createCorrelationId();

  let response: Response;
  try {
    response = await fetch(buildApiUrl(path), {
      ...init,
      headers: buildHeaders(init?.headers, correlationId),
    });
  } catch (error) {
    throw new HttpError(
      error instanceof Error ? error.message : 'Network request failed',
      0,
      null,
      {
        requestCorrelationId: correlationId,
        url: buildApiUrl(path),
      },
    );
  }

  const payload = await parseResponseBody(response);

  if (!response.ok) {
    const details = extractErrorDetails(payload, response, correlationId);
    throw new HttpError(
      details.message,
      response.status,
      payload,
      details,
    );
  }

  return payload as T;
}

export async function checkServiceStatus(
  path: string,
  label: string,
  successDetail = 'Endpoint responded successfully.',
): Promise<ServiceStatus> {
  try {
    const response = await fetch(buildApiUrl(path), {
      headers: buildHeaders(undefined, createCorrelationId()),
    });
    const payload = await parseResponseBody(response);
    const url = buildApiUrl(path);

    if (response.ok) {
      return {
        name: label,
        state: 'healthy',
        detail: successDetail,
        url,
        httpStatus: response.status,
        payload,
      };
    }

    return {
      name: label,
      state: 'reachable',
      detail: `Service responded with HTTP ${response.status}.`,
      url,
      httpStatus: response.status,
      payload,
    };
  } catch (error) {
    return {
      name: label,
      state: 'offline',
      detail: error instanceof Error ? error.message : 'Network error',
      url: buildApiUrl(path),
    };
  }
}

async function parseResponseBody(response: Response): Promise<unknown> {
  const contentType = response.headers.get('content-type') ?? '';

  if (contentType.includes('application/json')) {
    return response.json();
  }

  const text = await response.text();
  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function extractErrorMessage(payload: unknown, status: number) {
  if (payload && typeof payload === 'object' && 'message' in payload) {
    return String((payload as ApiErrorResponse).message);
  }

  if (typeof payload === 'string' && payload.trim()) {
    return payload;
  }

  return `Request failed with HTTP ${status}`;
}
