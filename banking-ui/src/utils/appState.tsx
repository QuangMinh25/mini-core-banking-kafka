import { createContext, useContext, useMemo, useState, type ReactNode } from 'react';
import type { TransferResponse } from '../types/transfer';

type AppStateValue = {
  lastTransfer: TransferResponse | null;
  setLastTransfer: (value: TransferResponse | null) => void;
};

const AppStateContext = createContext<AppStateValue | null>(null);

export function AppStateProvider({ children }: { children: ReactNode }) {
  const [lastTransfer, setLastTransfer] = useState<TransferResponse | null>(null);

  const value = useMemo(
    () => ({
      lastTransfer,
      setLastTransfer,
    }),
    [lastTransfer],
  );

  return <AppStateContext.Provider value={value}>{children}</AppStateContext.Provider>;
}

export function useAppState() {
  const context = useContext(AppStateContext);

  if (!context) {
    throw new Error('useAppState must be used inside AppStateProvider');
  }

  return context;
}
