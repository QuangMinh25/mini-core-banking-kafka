import {
  Activity,
  ArrowRightLeft,
  BellRing,
  Building2,
  DatabaseZap,
  Landmark,
  MessageSquareShare,
  WalletCards,
} from 'lucide-react';
import { useEffect, useState } from 'react';
import { checkServiceStatus } from '../api/httpClient';
import { ActionCard } from '../components/ActionCard';
import { JsonViewer } from '../components/JsonViewer';
import { LoadingState } from '../components/LoadingState';
import { PageHeader } from '../components/PageHeader';
import { StatCard } from '../components/StatCard';
import { StatusBadge } from '../components/StatusBadge';
import { demoAccounts, demoOutboxEvents, demoTransfer } from '../data/demoData';
import type { ServiceStatus } from '../types/common';
import { useAppState } from '../utils/appState';
import { formatCurrency } from '../utils/format';

const kafkaSteps = ['Transfer', 'Outbox', 'Kafka', 'Notification'];

export function DashboardPage() {
  const { lastTransfer } = useAppState();
  const [statuses, setStatuses] = useState<ServiceStatus[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;

    async function loadStatus() {
      setLoading(true);
      const result = await Promise.all([
        checkServiceStatus('core', 'Core Service'),
        checkServiceStatus('notification', 'Notification Service'),
      ]);

      if (mounted) {
        setStatuses(result);
        setLoading(false);
      }
    }

    void loadStatus();

    return () => {
      mounted = false;
    };
  }, []);

  const portfolioBalance = demoAccounts.reduce((sum, account) => sum + Number(account.balance), 0);
  const transferSummary = lastTransfer ?? demoTransfer;

  return (
    <div className="page-grid">
      <PageHeader
        title="Dashboard"
        description="A polished command view of balances, transfer activity, and the Kafka-backed lifecycle behind the banking test flow."
        helper="Use this page first to confirm service reachability, inspect the latest transfer context, and understand where the event-driven chain might break."
        actions={<StatusBadge status="Static + live hybrid" tone="info" subtle />}
      />

      {loading ? <LoadingState label="Checking service status..." /> : null}

      {!loading ? (
        <section className="stats-grid">
          <StatCard
            label="Portfolio balance"
            value={formatCurrency(portfolioBalance)}
            meta="Static demo aggregate across the seeded test accounts."
            icon={<WalletCards size={18} />}
            badge="Static demo"
          />
          <StatCard
            label="Active accounts"
            value={String(demoAccounts.length)}
            meta="Ready for account lookup, transfer, and ledger testing."
            icon={<Landmark size={18} />}
            badge="Demo"
          />
          {statuses.map((status) => (
            <StatCard
              key={status.name}
              label={status.name}
              value={status.state === 'healthy' ? 'Healthy' : status.state === 'reachable' ? 'Reachable' : 'Offline'}
              tone={
                status.state === 'healthy'
                  ? 'success'
                  : status.state === 'reachable'
                    ? 'warning'
                    : 'danger'
              }
              meta={status.detail}
              icon={<Activity size={18} />}
              badge="Live"
            />
          ))}
          <StatCard
            label="Last transfer result"
            value={transferSummary.referenceNo}
            tone={lastTransfer ? 'success' : 'default'}
            meta={
              lastTransfer
                ? `${formatCurrency(lastTransfer.amount, lastTransfer.currency)} | ${lastTransfer.status}`
                : 'Static demo shown until a real transfer is submitted.'
            }
            icon={<ArrowRightLeft size={18} />}
            badge={lastTransfer ? 'Live' : 'Static demo'}
          />
          <StatCard
            label="Kafka flow hint"
            value="Transfer -> Outbox -> Kafka -> Notification"
            meta={`${demoOutboxEvents.length} demo outbox events staged for visual testing.`}
            icon={<MessageSquareShare size={18} />}
            badge="Flow"
          />
        </section>
      ) : null}

      <div className="dashboard-grid">
        <section className="flow-panel card">
          <div className="card-heading">
            <div>
              <h4>Transfer event runway</h4>
              <p>Simple mental model for the journey from API request to consumer-side notification.</p>
            </div>
            <StatusBadge status="Observability focus" tone="info" subtle />
          </div>
          <div className="flow-steps">
            {kafkaSteps.map((step, index) => (
              <div className="flow-step" key={step}>
                <span>{step}</span>
                {index < kafkaSteps.length - 1 ? <ArrowRightLeft size={16} /> : null}
              </div>
            ))}
          </div>
          <div className="flow-callouts">
            <div>
              <DatabaseZap size={18} />
              <p>The core service persists account, transaction, and ledger data before the event leaves the transaction boundary.</p>
            </div>
            <div>
              <BellRing size={18} />
              <p>The notification service records processed events so re-delivery does not create duplicate business outcomes.</p>
            </div>
          </div>
        </section>

        <div className="action-grid">
          <ActionCard
            title="Create a test account"
            description="Seed fresh balances for transfer and statement experiments."
            icon={<Building2 size={18} />}
            meta={<StatusBadge status="Accounts" tone="neutral" subtle />}
            action={<a className="button button--primary" href="/accounts">Open Accounts</a>}
          />
          <ActionCard
            title="Trigger a transfer"
            description="Exercise idempotency, posting, and event emission in one path."
            icon={<ArrowRightLeft size={18} />}
            meta={<StatusBadge status="Transfer" tone="success" subtle />}
            action={<a className="button" href="/transfer">Open Transfer</a>}
          />
        </div>
      </div>

      <JsonViewer
        title="Dashboard raw state"
        value={{
          services: statuses,
          lastTransfer,
          demoAccounts,
          demoOutboxEvents,
        }}
      />
    </div>
  );
}
