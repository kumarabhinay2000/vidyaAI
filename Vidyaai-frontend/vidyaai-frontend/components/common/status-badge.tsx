import { cn } from "@/lib/utils";

type Variant =
    | "default"
    | "success"
    | "warning"
    | "danger"
    | "info"
    | "muted";

interface StatusBadgeProps {
    label: string;
    variant?: Variant;
    className?: string;
    dot?: boolean;
}

const VARIANTS: Record<Variant, string> = {
    default: "bg-surface-2 text-text-secondary border-border",
    success: "bg-success-muted text-success border-transparent",
    warning: "bg-warning-muted text-warning border-transparent",
    danger: "bg-danger-muted text-danger border-transparent",
    info: "bg-info-muted text-info border-transparent",
    muted: "bg-surface-2 text-text-tertiary border-border",
};

export function StatusBadge({
                                label,
                                variant = "default",
                                className,
                                dot = true,
                            }: StatusBadgeProps) {
    return (
        <span
            className={cn(
                "inline-flex items-center gap-1.5 rounded-full border px-2 py-0.5 text-[10px] font-medium uppercase tracking-wide",
                VARIANTS[variant],
                className,
            )}
        >
      {dot ? (
          <span className="h-1 w-1 rounded-full bg-current opacity-80" />
      ) : null}
            {label}
    </span>
    );
}