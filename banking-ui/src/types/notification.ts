export type NotificationLog = {
  eventId: string;
  referenceNo: string;
  fromAccountNo: string;
  toAccountNo: string;
  amount: number | string;
  currency: string;
  message: string;
  status: string;
  createdAt: string;
};
