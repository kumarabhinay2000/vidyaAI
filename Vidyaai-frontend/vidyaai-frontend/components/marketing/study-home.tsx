"use client";
import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { ArrowRight, BookOpen, Link2, Upload, Play } from "lucide-react";
import { ThemeToggle } from "@/components/common/theme-toggle";
import { Logo } from "@/components/common/logo";

export function SiteHeader() {
  return (
    <header className="sticky top-0 z-30 border-b border-border bg-background/95 backdrop-blur">
      <div className="site-container flex min-h-18 items-center justify-between gap-3">
        <Logo />
        <nav
          aria-label="Main navigation"
          className="hidden items-center gap-7 text-sm text-text-secondary md:flex"
        >
          <Link href="/#how-it-works">How it works</Link>
          <Link href="/#features">Features</Link>
          <Link href="/#sample-lesson">Sample lesson</Link>
        </nav>
        <div className="flex items-center gap-2">
          <ThemeToggle variant="simple" />
          <Link href="/login" className="quiet-link">
            Sign in
          </Link>
          <Link href="/register" className="primary-link text-sm">
            Get started
          </Link>
        </div>
      </div>
    </header>
  );
}
export function StartLecture() {
  const [url, setUrl] = useState("");
  const [error, setError] = useState("");
  const router = useRouter();
  function start(e: React.FormEvent) {
    e.preventDefault();
    try {
      const parsed = new URL(url);
      if (
        !/^https?:$/.test(parsed.protocol) ||
        ![
          "youtube.com",
          "www.youtube.com",
          "m.youtube.com",
          "youtu.be",
        ].includes(parsed.hostname)
      )
        throw new Error();
      router.push(`/register?videoUrl=${encodeURIComponent(url.trim())}`);
    } catch {
      setError("Paste a valid YouTube video link to continue.");
    }
  }
  return (
    <div className="mt-8">
      <form onSubmit={start} className="lecture-input">
        <label htmlFor="lecture-url" className="sr-only">
          YouTube lecture link
        </label>
        <Link2 className="h-4 w-4 shrink-0 text-text-tertiary" />
        <input
          id="lecture-url"
          value={url}
          onChange={(e) => {
            setUrl(e.target.value);
            setError("");
          }}
          placeholder="Paste a YouTube link"
          aria-invalid={!!error}
          aria-describedby={error ? "lecture-url-error" : undefined}
          className="min-w-0 flex-1 bg-transparent py-3 text-sm outline-none"
        />
        <button type="submit" className="primary-link shrink-0 text-sm">
          Add a lecture <ArrowRight className="hidden h-4 w-4 sm:block" />
        </button>
      </form>
      {error && (
        <p
          id="lecture-url-error"
          role="alert"
          className="mt-2 text-sm text-danger"
        >
          {error}
        </p>
      )}
      <Link
        href="/register?next=%2Fdashboard%2Fvideos%2Fupload"
        className="mt-4 inline-flex min-h-11 items-center gap-2 text-sm text-text-secondary"
      >
        <Upload className="h-4 w-4" /> Or upload a lecture video
      </Link>
    </div>
  );
}
const sampleNotes = (
  <>
    <h3 className="text-xl font-semibold">Your revision notes</h3>
    <h4 className="mt-6 font-medium">Mitosis: growth and repair</h4>
    <ul className="study-bullets">
      <li>One division produces two genetically identical daughter cells.</li>
      <li>Chromosome number stays the same: diploid to diploid.</li>
      <li>Used for growth, repair, and cell replacement.</li>
    </ul>
    <h4 className="mt-6 font-medium">Meiosis: genetic variation</h4>
    <ul className="study-bullets">
      <li>Two divisions produce four genetically different haploid cells.</li>
      <li>Crossing over exchanges genetic material between chromosomes.</li>
    </ul>
  </>
);
export function SampleLesson() {
  const [tab, setTab] = useState("Notes");
  const [answer, setAnswer] = useState<string | null>(null);
  return (
    <div className="sample-workspace">
      <div className="sample-video">
        <div className="flex items-center justify-between">
          <span className="text-xs uppercase tracking-widest">
            Biology / Sample lesson
          </span>
          <BookOpen className="h-4 w-4" />
        </div>
        <h3 className="mt-8 font-serif text-3xl">
          Cell division:
          <br />
          mitosis and meiosis
        </h3>
        <div className="mt-8 grid grid-cols-2 gap-6 border-y border-white/20 py-8">
          <div>
            <p className="font-serif text-xl">Mitosis</p>
            <p className="mt-3 font-mono text-2xl">◯ → ◯ ◯</p>
            <p className="mt-3 text-xs opacity-75">Growth & repair</p>
          </div>
          <div>
            <p className="font-serif text-xl">Meiosis</p>
            <p className="mt-3 font-mono text-2xl">◯ → ⁙</p>
            <p className="mt-3 text-xs opacity-75">Genetic variation</p>
          </div>
        </div>
        <p className="mt-6 text-xs opacity-75">
          Illustrated sample · Dr. Meera Rao · 24:18
        </p>
        <Link
          href="/register"
          className="mt-5 inline-flex min-h-11 items-center gap-2 text-sm"
        >
          <Play className="h-4 w-4" /> Bring your own lecture{" "}
          <ArrowRight className="h-4 w-4" />
        </Link>
      </div>
      <div className="min-w-0">
        <div
          className="flex overflow-x-auto border-b border-border"
          role="tablist"
          aria-label="Sample study material"
        >
          {["Summary", "Notes", "Quiz", "Ask AI"].map((name) => (
            <button
              key={name}
              role="tab"
              id={`sample-tab-${name.replaceAll(" ", "-")}`}
              aria-controls="sample-panel"
              aria-selected={tab === name}
              onClick={() => setTab(name)}
              className={`min-h-14 flex-1 whitespace-nowrap border-b-2 px-4 text-sm ${tab === name ? "border-accent text-accent" : "border-transparent text-text-secondary"}`}
            >
              {name}
            </button>
          ))}
        </div>
        <div
          id="sample-panel"
          role="tabpanel"
          aria-labelledby={`sample-tab-${tab.replaceAll(" ", "-")}`}
          tabIndex={0}
          className="p-6 sm:p-8"
        >
          {tab === "Notes" ? (
            sampleNotes
          ) : tab === "Summary" ? (
            <>
              <p className="eyebrow">Sample summary</p>
              <h3 className="mt-3 text-xl font-semibold">
                Two paths, different purposes.
              </h3>
              <ul className="study-bullets">
                <li>Mitosis helps an organism grow and repair itself.</li>
                <li>
                  Meiosis creates reproductive cells with half the chromosomes.
                </li>
                <li>
                  Crossing over creates new combinations of genetic material.
                </li>
              </ul>
            </>
          ) : tab === "Quiz" ? (
            <>
              <p className="eyebrow">Sample practice · not saved</p>
              <h3 className="my-5 text-lg font-medium">
                What is the main result of meiosis?
              </h3>
              {[
                "Two identical diploid cells",
                "Four different haploid cells",
              ].map((a) => (
                <button
                  key={a}
                  onClick={() => setAnswer(a)}
                  aria-pressed={answer === a}
                  className={`mb-3 block w-full rounded-md border px-4 py-3 text-left text-sm ${answer === a ? "border-accent bg-accent-muted" : "border-border"}`}
                >
                  {a}
                </button>
              ))}
              {answer && (
                <p
                  role="status"
                  className="mt-4 text-sm leading-6 text-text-secondary"
                >
                  {answer === "Four different haploid cells"
                    ? "That’s right."
                    : "Not quite."}{" "}
                  Meiosis produces four genetically different haploid cells
                  after two divisions.
                </p>
              )}
            </>
          ) : (
            <>
              <p className="eyebrow">Example conversation</p>
              <p className="mt-5 border-l-2 border-border pl-4 font-medium">
                Why does meiosis create genetic variation?
              </p>
              <p className="mt-5 text-sm leading-7 text-text-secondary">
                Crossing over exchanges DNA between homologous chromosomes.
                Independent assortment distributes chromosomes into different
                combinations.
              </p>
              <p className="mt-5 font-mono text-xs text-accent">
                Sample reference · 10:08 Crossing over
              </p>
              <p className="mt-6 text-xs text-text-tertiary">
                This is a written example, not a live AI response.
              </p>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
