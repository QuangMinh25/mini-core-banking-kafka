import { HttpError } from '../api/httpClient';

export function formatCurrency(value: number | string | null | undefined, currency = 'VND') {
  if (value === null || value === undefined || value === '') {
    return '-';
  }

  const numeric = typeof value === 'number' ? value : Number(value);
  if (Number.isNaN(numeric)) {
    return String(value);
  }

  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
    maximumFractionDigits: 2,
  }).format(numeric);
}

export function formatDateTime(value: string | null | undefined) {
  if (!value) {
    return '-';
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat('en-GB', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date);
}

export function getErrorDetails(error: unknown) {
  if (error instanceof HttpError) {
    return {
      message: error.message,
      code: error.code,
      correlationId: error.correlationId,
    };
  }

  if (error instanceof Error) {
    return {
      message: error.message,
    };
  }

  return {
    message: 'Unknown error',
  };
}

export function getErrorMessage(error: unknown) {
  return getErrorDetails(error).message;
}

export function toDebugValue(value: unknown) {
  if (value instanceof HttpError) {
    return value.toJSON();
  }

  if (value instanceof Error) {
    return {
      name: value.name,
      message: value.message,
      stack: value.stack,
    };
  }

  return value;
}
