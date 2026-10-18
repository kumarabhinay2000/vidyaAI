import { cn } from "@/lib/utils";

interface UserAvatarProps {
    firstName?: string | null;
    lastName?: string | null;
    imageUrl?: string | null;
    size?: "sm" | "md" | "lg";
    className?: string;
}

const SIZES = {
    sm: "h-7 w-7 text-[11px]",
    md: "h-9 w-9 text-xs",
    lg: "h-12 w-12 text-sm",
} as const;

function initials(first?: string | null, last?: string | null): string {
    const a = (first ?? "").trim();
    const b = (last ?? "").trim();
    if (a && b) return (a[0] + b[0]).toUpperCase();
    if (a) return a.slice(0, 2).toUpperCase();
    return "V";
}

export function UserAvatar({
                               firstName,
                               lastName,
                               imageUrl,
                               size = "md",
                               className,
                           }: UserAvatarProps) {
    return (
        <span
            className={cn(
                "inline-flex shrink-0 items-center justify-center overflow-hidden rounded-full border border-border bg-surface-2 font-medium text-text-primary",
                SIZES[size],
                className,
            )}
        >
      {imageUrl ? (
          // eslint-disable-next-line @next/next/no-img-element
          <img
              src={imageUrl}
              alt=""
              className="h-full w-full object-cover"
              referrerPolicy="no-referrer"
          />
      ) : (
          initials(firstName, lastName)
      )}
    </span>
    );
}