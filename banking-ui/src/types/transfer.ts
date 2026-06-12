export type TransferRequest = {
  fromAccountNo: string;
  toAccountNo: string;
  amount: string;
  currency: string;
};

export type TransferResponse = {
  referenceNo: string;
  status: string;
  fromAccountNo: string;
  toAccountNo: string;
  amount: number | string;
  currency: string;
};
