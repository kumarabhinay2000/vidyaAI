import Link from "next/link";
export default function Page() {
  return (
    <main id="main-content" className="site-container max-w-3xl py-20">
      <p className="eyebrow">VidyaAI</p>
      <h1 className="mt-5 font-serif text-4xl">Terms information</h1>
      <p className="mt-8 leading-8 text-text-secondary">
        Use VidyaAI with lecture material you are authorized to upload.
        AI-generated study material may contain errors; refer to the original
        lecture when checking important details.
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
