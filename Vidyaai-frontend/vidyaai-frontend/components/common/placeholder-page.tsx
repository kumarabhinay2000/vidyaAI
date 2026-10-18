import Link from "next/link";
import { ArrowRight } from "lucide-react";

interface PlaceholderPageProps {
    title: string;
    description: string;
    ctaLabel?: string;
    ctaHref?: string;
}

export function PlaceholderPage({
                                    title,
                                    description,
                                    ctaLabel,
                                    ctaHref,
                                }: PlaceholderPageProps) {
    return (
        <div className="mx-auto flex min-h-screen max-w-2xl flex-col justify-center px-6">
            <p className="text-xs font-medium uppercase tracking-widest text-text-tertiary">
                VidyaAI
            </p>
            <h1 className="mt-3 text-3xl font-semibold tracking-tight text-text-primary">
                {title}
            </h1>
            <p className="mt-3 text-sm text-text-secondary">{description}</p>

            {ctaHref && ctaLabel ? (
                <Link
                    href={ctaHref}
                    className="mt-8 inline-flex items-center gap-2 self-start rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-fg transition-colors hover:bg-accent-hover"
                >
                    {ctaLabel}
                    <ArrowRight className="h-4 w-4" />
                </Link>
            ) : null}
        </div>
    );
}