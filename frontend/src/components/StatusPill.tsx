interface StatusPillProps {
  active: boolean;
  label: string;
}

export function StatusPill({ active, label }: StatusPillProps) {
  return <span className={active ? 'status active' : 'status'}>{label}</span>;
}
