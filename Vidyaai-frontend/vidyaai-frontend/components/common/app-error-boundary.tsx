"use client";

import { Component, type ErrorInfo, type ReactNode } from "react";
import Link from "next/link";
import { AlertCircle, RefreshCw, Home } from "lucide-react";

interface Props {
    children: ReactNode;
    fallback?: ReactNode;
}

interface State {
    hasError: boolean;
    error?: Error;
}

/**
 * Follows UX Principle: Error Recovery
 * Every error message answers: what happened, why, and what to do.
 */
export class AppErrorBoundary extends Component<Props, State> {
    state: State = { hasError: false };

    static getDerivedStateFromError(error: Error): State {
        return { hasError: true, error };
    }

    componentDidCatch(error: Error, info: ErrorInfo): void {
        // eslint-disable-next-line no-console
        console.error("AppErrorBoundary:", error, info.componentStack);
    }

    render() {
        if (this.state.hasError) {
            return (
                this.props.fallback ?? (
                    <div className="flex min-h-screen items-center justify-center bg-background px-4">
                        <div className="w-full max-w-md rounded-2xl border border-border bg-surface p-6 text-center shadow-lg">
                            <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl bg-danger-muted text-danger">
                                <AlertCircle className="h-6 w-6" />
                            </div>

                            <p className="mt-4 font-mono text-[11px] uppercase tracking-wider text-text-tertiary">
                                Interface Error Encountered
                            </p>

                            <h1 className="mt-2 text-base font-semibold text-text-primary">
                                The view was interrupted while rendering
                            </h1>

                            <p className="mt-2 text-xs leading-relaxed text-text-secondary">
                                An unexpected condition halted this component. Your saved videos and notes are safe. Reload the interface to restore state.
                            </p>

                            {this.state.error?.message ? (
                                <p className="mt-3 rounded-lg border border-border bg-surface-2 p-2 font-mono text-[11px] text-text-tertiary break-all">
                                    {this.state.error.message}
                                </p>
                            ) : null}

                            <div className="mt-6 flex flex-col gap-2.5 sm:flex-row sm:justify-center">
                                <button
                                    type="button"
                                    onClick={() => {
                                        this.setState({ hasError: false });
                                        if (typeof window !== "undefined") {
                                            window.location.reload();
                                        }
                                    }}
                                    className="inline-flex min-h-[40px] items-center justify-center gap-2 rounded-lg bg-accent px-4 py-2 text-xs font-semibold text-accent-fg transition-colors hover:bg-accent-hover"
                                >
                                    <RefreshCw className="h-3.5 w-3.5" />
                                    <span>Reload page</span>
                                </button>

                                <Link
                                    href="/dashboard"
                                    className="inline-flex min-h-[40px] items-center justify-center gap-2 rounded-lg border border-border bg-surface-2 px-4 py-2 text-xs font-medium text-text-secondary transition-colors hover:bg-surface-3 hover:text-text-primary"
                                >
                                    <Home className="h-3.5 w-3.5" />
                                    <span>Return to dashboard</span>
                                </Link>
                            </div>
                        </div>
                    </div>
                )
            );
        }
        return this.props.children;
    }
}