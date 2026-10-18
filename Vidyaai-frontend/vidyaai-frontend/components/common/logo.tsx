import Link from "next/link";
import { BookOpen } from "lucide-react";

import { cn } from "@/lib/utils";

interface LogoProps {
  href?: string;
  className?: string;
  showWordmark?: boolean;
}

export function Logo({
  href = "/",
  className,
  showWordmark = true,
}: LogoProps) {
  return (
    <Link
      href={href}
      aria-label="VidyaAI home"
      className={cn(
        "inline-flex items-center gap-2 transition-opacity hover:opacity-90",
        className,
      )}
    >
      <BookOpen
        className="h-7 w-7 text-accent"
        strokeWidth={1.6}
        aria-hidden="true"
      />
      {showWordmark ? (
        <span className="text-sm font-semibold tracking-tight text-text-primary">
          VidyaAI
        </span>
      ) : null}
    </Link>
  );
}
