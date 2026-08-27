import type { ReactNode } from 'react';

interface DataPanelProps {
  title: string;
  description: string;
  children: ReactNode;
}

export function DataPanel({ title, description, children }: DataPanelProps) {
  return <section className="panel data-panel"><div className="panel-heading"><div><h2>{title}</h2><p>{description}</p></div><span className="subtle-badge">API data</span></div>{children}</section>;
}
