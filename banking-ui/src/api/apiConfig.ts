const defaultApiBaseUrl = 'http://localhost:8080';

export const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? defaultApiBaseUrl).replace(/\/+$/, '');

export function buildApiUrl(path: string) {
  return `${API_BASE_URL}${path}`;
}
