export type CreateAccountRequest = {
  accountNo: string;
  customerName: string;
  balance: string;
  currency: string;
};

export type AccountResponse = {
  accountNo: string;
  customerName: string;
  balance: number | string;
  currency: string;
  status: string;
  createdAt: string;
  updatedAt: string;
};
