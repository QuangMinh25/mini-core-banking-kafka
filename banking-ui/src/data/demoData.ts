import type { AccountResponse } from '../types/account';
import type { AuditLog } from '../types/audit';
import type { LedgerEntry, StatementResponse } from '../types/ledger';
import type { NotificationLog } from '../types/notification';
import type { OutboxEvent } from '../types/outbox';
import type { ProcessedEvent } from '../types/processedEvent';
import type { TransferResponse } from '../types/transfer';

export const demoAccounts: AccountResponse[] = [
  {
    accountNo: '100001',
    customerName: 'Nguyen Minh Quang',
    balance: 125430000,
    currency: 'VND',
    status: 'ACTIVE',
    createdAt: '2026-06-10T08:20:00Z',
    updatedAt: '2026-06-12T09:10:00Z',
  },
  {
    accountNo: '100002',
    customerName: 'Tran Thi Linh',
    balance: 28750000,
    currency: 'VND',
    status: 'ACTIVE',
    createdAt: '2026-06-09T04:45:00Z',
    updatedAt: '2026-06-12T08:42:00Z',
  },
];

export const demoTransfer: TransferResponse = {
  referenceNo: 'TXN-20260612-847521',
  status: 'COMPLETED',
  fromAccountNo: '100001',
  toAccountNo: '100002',
  amount: 5000000,
  currency: 'VND',
};

export const demoLedgerEntries: LedgerEntry[] = [
  {
    transactionReferenceNo: 'TXN-20260612-847521',
    accountNo: '100001',
    entryType: 'DEBIT',
    amount: 5000000,
    balanceAfter: 125430000,
    description: 'Transfer to 100002',
    createdAt: '2026-06-12T09:15:10Z',
  },
  {
    transactionReferenceNo: 'TXN-20260612-847521',
    accountNo: '100002',
    entryType: 'CREDIT',
    amount: 5000000,
    balanceAfter: 28750000,
    description: 'Incoming transfer from 100001',
    createdAt: '2026-06-12T09:15:10Z',
  },
  {
    transactionReferenceNo: 'TXN-20260611-193104',
    accountNo: '100001',
    entryType: 'DEBIT',
    amount: 2250000,
    balanceAfter: 130430000,
    description: 'Utility bill payment',
    createdAt: '2026-06-11T02:40:48Z',
  },
];

export const demoStatement: StatementResponse = {
  entries: demoLedgerEntries,
  page: 0,
  size: 20,
  totalElements: demoLedgerEntries.length,
  totalPages: 1,
};

export const demoOutboxEvents: OutboxEvent[] = [
  {
    eventId: 'evt_01JZ6Q2XTCKNQ0B4AJ8N7M5Y3S',
    topic: 'bank.transfer.completed',
    eventType: 'TRANSFER_COMPLETED',
    status: 'PUBLISHED',
    retryCount: 0,
    lastError: null,
    createdAt: '2026-06-12T09:15:11Z',
    publishedAt: '2026-06-12T09:15:13Z',
  },
  {
    eventId: 'evt_01JZ6Q41CXWQSNX1KB6BSH4PHN',
    topic: 'bank.transfer.completed',
    eventType: 'TRANSFER_COMPLETED',
    status: 'RETRYING',
    retryCount: 2,
    lastError: 'Kafka timeout on broker-2',
    createdAt: '2026-06-12T08:54:02Z',
    publishedAt: null,
  },
  {
    eventId: 'evt_01JZ6Q5MM3Y00MPXC8PW7AJ9J0',
    topic: 'bank.audit.transfer',
    eventType: 'AUDIT_LOG_CREATED',
    status: 'PENDING',
    retryCount: 0,
    lastError: null,
    createdAt: '2026-06-12T08:47:55Z',
    publishedAt: null,
  },
];

export const demoNotifications: NotificationLog[] = [
  {
    eventId: 'evt_01JZ6Q2XTCKNQ0B4AJ8N7M5Y3S',
    referenceNo: 'TXN-20260612-847521',
    fromAccountNo: '100001',
    toAccountNo: '100002',
    amount: 5000000,
    currency: 'VND',
    message: 'Transfer completed. Beneficiary notified.',
    status: 'DELIVERED',
    createdAt: '2026-06-12T09:15:18Z',
  },
  {
    eventId: 'evt_01JZ6Q1SJQF1EZ7MGE26ZQSK00',
    referenceNo: 'TXN-20260612-102411',
    fromAccountNo: '100003',
    toAccountNo: '100004',
    amount: 1500000,
    currency: 'VND',
    message: 'Notification queued for retry.',
    status: 'QUEUED',
    createdAt: '2026-06-12T08:21:48Z',
  },
];

export const demoProcessedEvents: ProcessedEvent[] = [
  {
    eventId: 'evt_01JZ6Q2XTCKNQ0B4AJ8N7M5Y3S',
    topic: 'bank.transfer.completed',
    processedAt: '2026-06-12T09:15:17Z',
  },
  {
    eventId: 'evt_01JZ6Q1SJQF1EZ7MGE26ZQSK00',
    topic: 'bank.transfer.completed',
    processedAt: '2026-06-12T08:21:45Z',
  },
];

export const demoAuditLogs: AuditLog[] = [
  {
    action: 'TRANSFER_SUBMITTED',
    resourceType: 'TRANSFER',
    resourceId: 'TXN-20260612-847521',
    status: 'SUCCESS',
    errorMessage: null,
    createdAt: '2026-06-12T09:15:10Z',
  },
  {
    action: 'OUTBOX_PUBLISH_ATTEMPT',
    resourceType: 'OUTBOX_EVENT',
    resourceId: 'evt_01JZ6Q41CXWQSNX1KB6BSH4PHN',
    status: 'RETRYING',
    errorMessage: 'Kafka timeout on broker-2',
    createdAt: '2026-06-12T08:54:07Z',
  },
  {
    action: 'ACCOUNT_LOOKUP',
    resourceType: 'ACCOUNT',
    resourceId: '100001',
    status: 'SUCCESS',
    errorMessage: null,
    createdAt: '2026-06-12T07:43:12Z',
  },
];
