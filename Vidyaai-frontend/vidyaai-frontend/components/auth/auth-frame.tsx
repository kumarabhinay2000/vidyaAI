import Link from "next/link";
export function AuthFrame({
  title,
  description,
  children,
  footer,
}: {
  title: string;
  description: string;
  children: React.ReactNode;
  footer: React.ReactNode;
}) {
  return (
    <main id="main-content" className="site-container auth-grid">
      <div className="auth-copy">
        <p className="eyebrow mb-5">Your study space</p>
        <h1>{title}</h1>
        <p className="mt-4 text-base leading-7 text-text-secondary">
          {description}
        </p>
        <div className="mt-8">{children}</div>
        <div className="mt-7 text-sm text-text-secondary">{footer}</div>
      </div>
      <aside className="auth-aside">
        <p className="eyebrow">A little clarity goes a long way</p>
        <blockquote>
          Same questions.
          <br />A deeper understanding.
        </blockquote>
        <div className="mt-10 border-y border-border py-6">
          <p className="text-sm font-medium">From the sample notebook</p>
          <h2 className="mt-4 font-serif text-2xl">Mitosis & meiosis</h2>
          <ul className="study-bullets">
            <li>Mitosis supports growth and repair.</li>
            <li>Meiosis creates four haploid cells.</li>
            <li>Crossing over introduces genetic variation.</li>
          </ul>
          <Link
            href="/#sample-lesson"
            className="mt-6 inline-flex min-h-11 items-center text-sm text-accent"
          >
            Explore the sample lesson →
          </Link>
        </div>
        <p className="mt-5 text-xs text-text-tertiary">
          Fictional sample lesson · Biology · 24:18
        </p>
      </aside>
    </main>
  );
}
