import Link from "next/link";
import {
  ArrowRight,
  BookOpen,
  FileText,
  ListChecks,
  MessageCircle,
} from "lucide-react";
import {
  SiteHeader,
  StartLecture,
  SampleLesson,
} from "@/components/marketing/study-home";

const features = [
  {
    icon: FileText,
    title: "Clear summaries",
    text: "Find the main ideas, then return to the details that matter.",
  },
  {
    icon: BookOpen,
    title: "Organized notes",
    text: "Revisit key concepts in a quieter, more readable workspace.",
  },
  {
    icon: ListChecks,
    title: "Practice quizzes",
    text: "Check your understanding and learn from your mistakes.",
  },
  {
    icon: MessageCircle,
    title: "Questions in context",
    text: "Ask about your lecture and explore the ideas behind it.",
  },
];

export default function HomePage() {
  return (
    <div className="marketing-page">
      <SiteHeader />
      <main id="main-content">
        <section className="site-container hero-grid">
          <div>
            <p className="eyebrow">
              A little more clarity. A better way to study.
            </p>
            <h1 className="editorial-title">
              A lecture is just
              <br className="hidden sm:block" /> the beginning.
            </h1>
            <p className="hero-description">
              Make sense of what you watch. Keep useful notes, ask questions,
              and check what you remember.
            </p>
            <StartLecture />
            <p className="mt-5 text-sm text-text-tertiary">
              Your videos. One focused study space.
            </p>
          </div>
          <div
            className="hero-excerpt"
            aria-label="Sample biology lecture notes"
          >
            <div className="flex items-center justify-between border-b border-border pb-4">
              <span className="eyebrow">From the lecture notebook</span>
              <span className="font-mono text-xs text-text-tertiary">
                12:06 / 24:18
              </span>
            </div>
            <h2 className="mt-7 font-serif text-3xl">
              One cell. Two different paths.
            </h2>
            <div className="cell-sketch" aria-hidden="true">
              <span>2n</span>
              <i>→</i>
              <span>2n</span>
              <span>2n</span>
              <b>Mitosis · growth & repair</b>
            </div>
            <p className="reading-copy">
              Mitosis preserves chromosome number. Meiosis halves it, creating
              the variation that makes each organism unique.
            </p>
            <div className="mt-6 flex items-center justify-between gap-3 border-t border-border pt-4 text-xs text-text-secondary">
              <span>Cell division: mitosis and meiosis</span>
              <span className="shrink-0 rounded bg-accent-muted px-2 py-1 text-accent">
                Sample lesson
              </span>
            </div>
          </div>
        </section>
        <section id="sample-lesson" className="site-container public-section">
          <div className="section-heading">
            <div>
              <p className="eyebrow">A sample, before you start</p>
              <h2>From watching to understanding.</h2>
            </div>
            <p>A lecture, its key ideas, and room for your questions.</p>
          </div>
          <SampleLesson />
        </section>
        <section id="how-it-works" className="site-container public-section">
          <p className="eyebrow">A simple rhythm</p>
          <h2 className="section-title">
            One lecture. Three thoughtful steps.
          </h2>
          <div className="mt-10 grid gap-8 md:grid-cols-3">
            {[
              [
                "01",
                "Bring a lecture",
                "Paste a YouTube link or upload a lecture video.",
              ],
              [
                "02",
                "Make sense of it",
                "Read the summary, explore your notes, and ask a question.",
              ],
              [
                "03",
                "Put it into practice",
                "Try a quiz, revisit a concept, and keep learning.",
              ],
            ].map(([number, title, text]) => (
              <div key={number} className="study-step flex gap-5">
                <span className="step-number font-mono text-sm text-accent">{number}</span>
                <div>
                  <h3 className="font-medium">{title}</h3>
                  <p className="mt-2 text-sm leading-6 text-text-secondary">
                    {text}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </section>
        <section
          id="features"
          className="site-container public-section feature-section"
        >
          <div>
            <p className="eyebrow">Purposeful tools</p>
            <h2 className="section-title">
              Everything you need.
              <br />
              Nothing in the way.
            </h2>
            <p className="mt-5 max-w-sm text-text-secondary">
              A focused workspace for understanding, practice, and revision.
            </p>
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            {features.map(({ icon: Icon, title, text }) => (
              <div key={title} className="feature-card">
                <span className="feature-icon" aria-hidden="true">
                  <Icon className="h-5 w-5" strokeWidth={1.6} />
                </span>
                <h3 className="font-medium">{title}</h3>
                <p className="mt-2 text-sm leading-6 text-text-secondary">
                  {text}
                </p>
              </div>
            ))}
          </div>
        </section>
        <section id="faq" className="site-container public-section faq-section">
          <div>
            <p className="eyebrow">Before you begin</p>
            <h2 className="section-title">Good questions.</h2>
          </div>
          <div>
            {[
              [
                "What videos can I use?",
                "Add a YouTube link or upload an MP4, WebM, MKV, MOV, or AVI lecture video, up to 2 GB.",
              ],
              [
                "Can I try a sample first?",
                "Yes. Explore the sample lesson above without creating an account. It is clearly labeled sample content.",
              ],
              [
                "How does the AI tutor work?",
                "Choose a lecture and ask questions about its content. Responses depend on the available transcript; check important details against the original lecture.",
              ],
            ].map(([q, a]) => (
              <details key={q} className="group border-b border-border py-5">
                <summary className="flex cursor-pointer list-none items-center justify-between gap-4 font-medium">
                  {q}
                  <span
                    className="text-xl font-normal text-text-tertiary group-open:rotate-45"
                    aria-hidden="true"
                  >
                    +
                  </span>
                </summary>
                <p className="mt-4 max-w-prose text-sm leading-7 text-text-secondary">
                  {a}
                </p>
              </details>
            ))}
          </div>
        </section>
        <section className="site-container pb-20">
          <div className="final-invitation">
            <div>
              <h2 className="font-serif text-3xl sm:text-4xl">
                What are you learning next?
              </h2>
              <p className="mt-3 text-text-secondary">
                Bring a video. Build your understanding.
              </p>
            </div>
            <Link href="/register" className="primary-link">
              Add your first lecture <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
        </section>
      </main>
      <footer className="border-t border-border">
        <div className="site-container grid gap-10 py-12 sm:grid-cols-4">
          <div className="sm:col-span-2">
            <Link href="/" className="flex items-center gap-2 font-semibold">
              <BookOpen className="h-6 w-6 text-accent" />
              VidyaAI
            </Link>
            <p className="mt-3 text-sm text-text-secondary">
              A clearer way to learn from video.
            </p>
          </div>
          <div>
            <p className="eyebrow">Product</p>
            <div className="mt-4 flex flex-col gap-3 text-sm text-text-secondary">
              <a href="#features">Features</a>
              <a href="#how-it-works">How it works</a>
              <a href="#sample-lesson">Sample lesson</a>
            </div>
          </div>
          <div>
            <p className="eyebrow">Your space</p>
            <div className="mt-4 flex flex-col gap-3 text-sm text-text-secondary">
              <Link href="/login">Sign in</Link>
              <Link href="/register">Create account</Link>
              <Link href="/privacy">Privacy</Link>
              <Link href="/terms">Terms</Link>
            </div>
          </div>
        </div>
        <div className="site-container border-t border-border py-6 text-xs text-text-tertiary">
          © 2026 VidyaAI. A little more understanding, every day.
        </div>
      </footer>
    </div>
  );
}
