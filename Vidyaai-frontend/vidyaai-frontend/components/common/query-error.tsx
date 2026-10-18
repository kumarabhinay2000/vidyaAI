import { AlertCircle } from "lucide-react";
export function QueryError({
  title = "We couldn’t load this view.",
  retry,
}: {
  title?: string;
  retry: () => void;
}) {
  return (
    <div
      role="alert"
      className="rounded-lg border border-border bg-surface p-8"
    >
      <AlertCircle className="h-6 w-6 text-rust" />
      <h2 className="mt-4 text-lg font-medium">{title}</h2>
      <p className="mt-2 text-sm text-text-secondary">
        Check your connection and try again. Your saved work is unchanged.
      </p>
      <button
        type="button"
        onClick={retry}
        className="primary-link mt-5 text-sm"
      >
        Try again
      </button>
    </div>
  );
}
