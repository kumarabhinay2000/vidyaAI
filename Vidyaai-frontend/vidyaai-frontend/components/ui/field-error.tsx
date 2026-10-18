import { cn } from "@/lib/utils";

interface FieldErrorProps {
    message?: string;
    className?: string;
}

export function FieldError({ message, className }: FieldErrorProps) {
    if (!message) return null;
    return (
        <p className={cn("mt-1 text-[11px] text-danger", className)} role="alert">
            {message}
        </p>
    );
}