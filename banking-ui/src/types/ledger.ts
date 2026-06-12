export type StatementQuery = {
  accountNo: string;
  fromDate: string;
  toDate: string;
  page: number;
  size: number;
};

export type LedgerEntry = {
  transactionReferenceNo: string;
  accountNo: string;
  entryType: string;
  amount: number | string;
  balanceAfter: number | string;
  description: string;
  createdAt: string;
};

export type StatementResponse = {
  entries: LedgerEntry[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};
