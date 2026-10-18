import Link from "next/link";
import { Logo } from "@/components/common/logo";
export default function NotFound() {
  return (
    <main
      id="main-content"
      className="site-container flex min-h-screen flex-col items-start justify-center py-16"
    >
      <Logo />
      <p className="eyebrow mt-16">404 · Page not found</p>
      <h1 className="mt-4 font-serif text-5xl">This page isn’t here.</h1>
      <p className="mt-5 text-text-secondary">
        It may have moved, or the link may be incomplete.
      </p>
      <Link className="primary-link mt-8" href="/dashboard/videos">
        Return to library
      </Link>
      <Link className="quiet-link mt-2" href="/">
        Back home
      </Link>
    </main>
  );
}
