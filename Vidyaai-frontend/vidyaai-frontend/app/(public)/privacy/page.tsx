import Link from "next/link";
export default function Page() {
  return (
    <main id="main-content" className="site-container max-w-3xl py-20">
      <p className="eyebrow">VidyaAI</p>
      <h1 className="mt-5 font-serif text-4xl">Privacy information</h1>
      <p className="mt-8 leading-8 text-text-secondary">
        Your signed-in session and appearance preference are stored in this
        browser. Lecture content is sent to the configured VidyaAI services when
        you upload or request processing.
      </p>
      <p className="mt-6 text-sm leading-7 text-text-secondary">
        A complete published policy has not been provided for this deployment.
        This page describes the current interface and does not replace that
        policy.
      </p>
      <Link className="quiet-link mt-8" href="/">
        ← Back home
      </Link>
    </main>
  );
}
