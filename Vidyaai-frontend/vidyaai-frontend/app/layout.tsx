import type { Metadata, Viewport } from "next";

import { Toaster } from "sonner";

import { AppProviders } from "@/components/providers/app-providers";
import { AppErrorBoundary } from "@/components/common/app-error-boundary";

import "./globals.css";

export const metadata: Metadata = {
  title: {
    default: "VidyaAI — Turn any video into a learning experience",
    template: "%s · VidyaAI",
  },
  description:
    "AI-powered summaries, notes, quizzes, and an interactive tutor for any video.",
  applicationName: "VidyaAI",
  metadataBase: new URL(
    process.env.NEXT_PUBLIC_APP_URL ?? "http://localhost:3000",
  ),
  icons: { icon: "/favicon.ico" },
};

export const viewport: Viewport = {
  themeColor: "#0a0a0b",
  width: "device-width",
  initialScale: 1,
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="study-app dark" suppressHydrationWarning>
      <body className="min-h-screen bg-background text-foreground antialiased">
        <a href="#main-content" className="skip-link">
          Skip to content
        </a>
        <AppErrorBoundary>
          <AppProviders>
            {children}
            <Toaster
              theme="system"
              position="bottom-right"
              toastOptions={{
                classNames: {
                  toast:
                    "bg-surface-2 border border-border text-text-primary shadow-lg",
                  description: "text-text-secondary",
                  actionButton: "bg-accent text-accent-fg",
                  cancelButton: "bg-surface-3 text-text-secondary",
                },
              }}
            />
          </AppProviders>
        </AppErrorBoundary>
      </body>
    </html>
  );
}
