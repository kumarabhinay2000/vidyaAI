import type { ReactNode } from "react";

import { cn } from "@/lib/utils";

interface SectionCardProps {
  title?: string;
  description?: ReactNode;
  actions?: ReactNode;
  children?: ReactNode;
  className?: string;
}

export function SectionCard({
  title,
  description,
  actions,
  children,
  className,
}: SectionCardProps) {
  const hasHeader = Boolean(title || description || actions);

  return (
    <section className={cn("min-w-0", className)}>
      {hasHeader ? (
        <div className="mb-4 flex flex-wrap items-start justify-between gap-3">
          <div className="min-w-0 flex-1">
            {title ? (
              <h2 className="text-lg font-semibold tracking-tight text-text-primary">
                {title}
              </h2>
            ) : null}
            {description ? (
              <div className="mt-2 text-sm text-text-secondary">
                {description}
              </div>
            ) : null}
          </div>
          {actions ? <div className="shrink-0">{actions}</div> : null}
        </div>
      ) : null}
      {children}
    </section>
  );
}
