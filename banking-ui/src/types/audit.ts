export type AuditLog = {
  action: string;
  resourceType: string;
  resourceId: string;
  status: string;
  errorMessage: string | null;
  createdAt: string;
};
