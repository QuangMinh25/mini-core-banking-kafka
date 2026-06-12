import { PanelLeftClose, PanelLeftOpen } from 'lucide-react';
import { NavLink } from 'react-router-dom';
import { navItems } from './navigation';

type SidebarProps = {
  mobileOpen: boolean;
  onClose: () => void;
  onToggle: () => void;
};

export function Sidebar({ mobileOpen, onClose, onToggle }: SidebarProps) {
  return (
    <>
      <button
        className="sidebar-toggle"
        type="button"
        onClick={onToggle}
        aria-label={mobileOpen ? 'Close navigation' : 'Open navigation'}
      >
        {mobileOpen ? <PanelLeftClose size={18} /> : <PanelLeftOpen size={18} />}
      </button>
      <aside className={`sidebar ${mobileOpen ? 'is-open' : ''}`}>
        <div className="sidebar__brand">
          <div className="sidebar__logo">MB</div>
          <div>
            <p className="sidebar__eyebrow">Mini Core Banking</p>
            <h1>Stellar Bank</h1>
            <small className="sidebar__caption">Polished demo workspace</small>
          </div>
        </div>
        <nav className="sidebar__nav">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  `sidebar__link${isActive ? ' sidebar__link--active' : ''}`
                }
                onClick={onClose}
              >
                <Icon size={18} />
                <span>
                  <strong>{item.label}</strong>
                  <small>{item.description}</small>
                </span>
              </NavLink>
            );
          })}
        </nav>
        <div className="sidebar__footer">
          <p>Local-only UI for testing API, idempotency, and Kafka flow behavior.</p>
          <strong>No auth enabled</strong>
        </div>
      </aside>
      {mobileOpen ? <button className="sidebar-backdrop" type="button" onClick={onClose} /> : null}
    </>
  );
}
