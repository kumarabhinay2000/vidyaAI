import { cn } from "@/lib/utils";

interface ChartShellProps {
    title: string;
    description?: string;
    children: React.ReactNode;
    height?: number;
    className?: string;
}

export function ChartShell({
                               title,
                               description,
                               children,
                               height = 240,
                               className,
                           }: ChartShellProps) {
    return (
        <div
            className={cn(
                "rounded-lg border border-border bg-surface/40",
                className,
            )}
        >
            <div className="border-b border-border px-4 py-3">
                <p className="text-sm font-medium text-text-primary">{title}</p>
                {description ? (
                    <p className="mt-0.5 text-[11px] text-tertiary text-text-tertiary">
                        {description}
                    </p>
                ) : null}
            </div>
            <div className="px-2 py-4" style={{ height }}>
                {children}
            </div>
        </div>
    );
}