import { Outlet, useLocation } from 'react-router-dom';
import { useMemo, useState } from 'react';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';
import { pageMetaByPath } from './navigation';

export function AppLayout() {
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  const meta = useMemo(
    () =>
      pageMetaByPath[location.pathname] ?? {
        title: 'Internet Banking Lab',
        description: 'Explore local service behavior and Kafka-backed flow',
      },
    [location.pathname],
  );

  return (
    <div className="app-shell">
      <Sidebar
        mobileOpen={mobileOpen}
        onClose={() => setMobileOpen(false)}
        onToggle={() => setMobileOpen((current) => !current)}
      />
      <div className="app-shell__content">
        <div className="app-shell__content-inner">
          <Topbar title={meta.title} description={meta.description} />
          <main className="page-shell">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}
