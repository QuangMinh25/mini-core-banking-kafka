export type OutboxEvent = {
  eventId: string;
  topic: string;
  eventType: string;
  status: string;
  retryCount: number;
  lastError: string | null;
  createdAt: string;
  publishedAt: string | null;
};
