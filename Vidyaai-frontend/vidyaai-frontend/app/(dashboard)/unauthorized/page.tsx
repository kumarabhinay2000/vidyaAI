import Link from "next/link";
export default function Page() {
  return (
    <main id="main-content" className="py-16">
      <p className="eyebrow">Access restricted</p>
      <h1 className="mt-5 text-3xl font-medium">
        You don’t have access to this page.
      </h1>
      <p className="mt-4 text-text-secondary">
        Return to your learning workspace to continue.
      </p>
      <Link href="/dashboard" className="primary-link mt-7">
        Back to overview
      </Link>
    </main>
  );
}
