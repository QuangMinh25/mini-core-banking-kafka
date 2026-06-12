import type { ApiErrorResponse, ServiceStatus } from '../types/common';

const serviceBaseUrls = {
  core: 'http://localhost:8081',
  notification: 'http://localhost:8082',
} as const;

export type ServiceName = keyof typeof serviceBaseUrls;

export class HttpError extends Error {
  status: number;
  payload: unknown;

  constructor(message: string, status: number, payload: unknown) {
    super(message);
    this.name = 'HttpError';
    this.status = status;
    this.payload = payload;
  }
}

export function getServiceBaseUrl(service: ServiceName) {
  return serviceBaseUrls[service];
}

export async function requestJson<T>(
  service: ServiceName,
  path: string,
  init?: RequestInit,
): Promise<T> {
  const response = await fetch(`${getServiceBaseUrl(service)}${path}`, {
    ...init,
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
  });

  const payload = await parseResponseBody(response);

  if (!response.ok) {
    const message = extractErrorMessage(payload, response.status);
    throw new HttpError(message, response.status, payload);
  }

  return payload as T;
}

export async function checkServiceStatus(
  service: ServiceName,
  label: string,
): Promise<ServiceStatus> {
  try {
    const response = await fetch(`${getServiceBaseUrl(service)}/actuator/health`, {
      headers: { Accept: 'application/json' },
    });
    const payload = await parseResponseBody(response);

    if (response.ok) {
      return {
        name: label,
        state: 'healthy',
        detail: 'Health endpoint responded successfully.',
        httpStatus: response.status,
        payload,
      };
    }

    return {
      name: label,
      state: 'reachable',
      detail: `Service responded with HTTP ${response.status}.`,
      httpStatus: response.status,
      payload,
    };
  } catch (error) {
    return {
      name: label,
      state: 'offline',
      detail: error instanceof Error ? error.message : 'Network error',
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
