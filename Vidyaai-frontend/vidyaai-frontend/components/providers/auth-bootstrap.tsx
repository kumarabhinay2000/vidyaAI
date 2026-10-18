"use client";

import { useEffect } from "react";

import { useAuthStore } from "@/stores/auth-store";
import { authApi } from "@/lib/api/auth";

interface AuthBootstrapProps {
    children: React.ReactNode;
}

/**
 * On app boot, if a refresh token exists in the auth store,
 * try to hydrate the current user session by calling /auth/refresh-token
 * via the API client. If it fails, we clear auth and continue as guest.
 */
export function AuthBootstrap({ children }: AuthBootstrapProps) {
    const setSession = useAuthStore((s) => s.setSession);
    const clear = useAuthStore((s) => s.clear);

    useEffect(() => {
        let cancelled = false;

        const bootstrap = async () => {
            const token = useAuthStore.getState().accessToken;
            if (!token) return;

            try {
                const session = await authApi.refresh();
                if (!cancelled && session) setSession(session);
            } catch {
                if (!cancelled) clear();
            }
        };

        void bootstrap();

        return () => {
            cancelled = true;
        };
    }, [setSession, clear]);

    return <>{children}</>;
}