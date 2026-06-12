import {
  ArrowRightLeft,
  Bell,
  BookOpenText,
  Boxes,
  ClipboardList,
  CreditCard,
  LayoutDashboard,
  ScrollText,
  type LucideIcon,
} from 'lucide-react';

export type NavItem = {
  path: string;
  label: string;
  description: string;
  icon: LucideIcon;
};

export const navItems: NavItem[] = [
  {
    path: '/dashboard',
    label: 'Dashboard',
    description: 'Quick health and Kafka flow overview',
    icon: LayoutDashboard,
  },
  {
    path: '/accounts',
    label: 'Accounts',
    description: 'Create and inspect account records',
    icon: CreditCard,
  },
  {
    path: '/transfer',
    label: 'Transfer',
    description: 'Test idempotent transfer requests',
    icon: ArrowRightLeft,
  },
  {
    path: '/statement',
    label: 'Statement',
    description: 'Explore ledger-backed account history',
    icon: BookOpenText,
  },
  {
    path: '/outbox-events',
    label: 'Outbox Events',
    description: 'Inspect outbox publishing visibility',
    icon: Boxes,
  },
  {
    path: '/notifications',
    label: 'Notifications',
    description: 'Review consumer-side notification logs',
    icon: Bell,
  },
  {
    path: '/processed-events',
    label: 'Processed Events',
    description: 'Understand duplicate event protection',
    icon: ScrollText,
  },
  {
    path: '/audit-logs',
    label: 'Audit Logs',
    description: 'Check action-level trace records',
    icon: ClipboardList,
  },
];

export const pageMetaByPath = Object.fromEntries(
  navItems.map((item) => [item.path, { title: item.label, description: item.description }]),
);
