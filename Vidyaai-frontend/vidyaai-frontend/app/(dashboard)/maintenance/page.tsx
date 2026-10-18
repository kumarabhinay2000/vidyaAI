import Link from "next/link";
export default function Page() {
  return (
    <main id="main-content" className="py-16">
      <p className="eyebrow">A short interruption</p>
      <h1 className="mt-5 text-3xl font-medium">We’re getting things ready.</h1>
      <p className="mt-4 text-text-secondary">
        This service is temporarily unavailable. Please try again shortly.
      </p>
      <Link href="/dashboard" className="primary-link mt-7">
        Try your overview
      </Link>
      <Link href="/" className="quiet-link ml-3">
        Back home
      </Link>
    </main>
  );
}
