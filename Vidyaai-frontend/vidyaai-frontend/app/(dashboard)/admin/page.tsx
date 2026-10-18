import Link from "next/link";
export default function Page() {
  return (
    <div className="grid gap-6 md:grid-cols-2">
      {[
        ["Users", "View the registered user directory.", "/admin/users"],
        [
          "Videos",
          "Inspect lectures available to your account.",
          "/admin/videos",
        ],
      ].map(([title, description, href]) => (
        <Link
          key={href}
          href={href}
          className="rounded-lg border border-border bg-surface p-6"
        >
          <h2 className="text-xl font-medium">{title}</h2>
          <p className="mt-3 text-sm text-text-secondary">{description}</p>
          <span className="mt-6 inline-block text-sm text-accent">
            Open directory →
          </span>
        </Link>
      ))}
      <p className="text-sm leading-7 text-text-secondary md:col-span-2">
        Platform-wide health monitoring and moderation actions are not connected
        in this version. Counts are shown only where live data is available.
      </p>
    </div>
  );
}
