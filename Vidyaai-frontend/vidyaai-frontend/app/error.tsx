"use client";
import Link from "next/link";
export default function ErrorPage({ reset }: { reset: () => void }) {
  return (
    <main id="main-content" className="site-container py-24">
      <p className="eyebrow">Something interrupted this view</p>
      <h1 className="mt-5 font-serif text-4xl">We couldn’t load this view.</h1>
      <p className="mt-4 text-text-secondary">
        Your saved work is unchanged. Please try again.
      </p>
      <button onClick={reset} className="primary-link mt-7">
        Try again
      </button>
      <Link href="/dashboard" className="quiet-link ml-3">
        Back to overview
      </Link>
    </main>
  );
}
