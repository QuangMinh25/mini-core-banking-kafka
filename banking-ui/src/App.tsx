import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from './layout/AppLayout';
import { AppStateProvider } from './utils/appState';
import { AccountsPage } from './pages/AccountsPage';
import { AuditLogsPage } from './pages/AuditLogsPage';
import { DashboardPage } from './pages/DashboardPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { OutboxEventsPage } from './pages/OutboxEventsPage';
import { ProcessedEventsPage } from './pages/ProcessedEventsPage';
import { StatementPage } from './pages/StatementPage';
import { TransferPage } from './pages/TransferPage';

export default function App() {
  return (
    <AppStateProvider>
      <Routes>
        <Route element={<AppLayout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/accounts" element={<AccountsPage />} />
          <Route path="/transfer" element={<TransferPage />} />
          <Route path="/statement" element={<StatementPage />} />
          <Route path="/outbox-events" element={<OutboxEventsPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/processed-events" element={<ProcessedEventsPage />} />
          <Route path="/audit-logs" element={<AuditLogsPage />} />
        </Route>
      </Routes>
    </AppStateProvider>
  );
}
