export function LoadingState() {
  return <div className="table-state">Loading live operations data…</div>;
}

export function EmptyState({ message }: { message: string }) {
  return <div className="table-state">{message}</div>;
}
