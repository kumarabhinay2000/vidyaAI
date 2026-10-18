"use client";

import { QueryProvider } from "./query-provider";
import { ThemeProvider } from "./theme-provider";
import { AuthBootstrap } from "./auth-bootstrap";
import { NotificationStreamProvider } from "./notification-stream-provider";

interface AppProvidersProps {
    children: React.ReactNode;
}

export function AppProviders({ children }: AppProvidersProps) {
    return (
        <ThemeProvider>
            <QueryProvider>
                <AuthBootstrap>
                    <NotificationStreamProvider>{children}</NotificationStreamProvider>
                </AuthBootstrap>
            </QueryProvider>
        </ThemeProvider>
    );
}