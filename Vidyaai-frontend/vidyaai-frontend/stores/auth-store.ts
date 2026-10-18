import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";

import type { AuthSession, User } from "@/types/user";

interface AuthState {
    accessToken: string | null;
    refreshToken: string | null;
    user: User | null;
    hydrated: boolean;

    setSession: (session: AuthSession) => void;
    setUser: (user: User) => void;
    clear: () => void;
    setHydrated: (hydrated: boolean) => void;

    isAuthenticated: () => boolean;
    isAdmin: () => boolean;
    isTeacher: () => boolean;
    isStudent: () => boolean;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set, get) => ({
            accessToken: null,
            refreshToken: null,
            user: null,
            hydrated: false,

            setSession: (session) =>
                set({
                    accessToken: session.accessToken,
                    refreshToken: session.refreshToken,
                    user: session.user,
                }),

            setUser: (user) => set({ user }),

            clear: () =>
                set({
                    accessToken: null,
                    refreshToken: null,
                    user: null,
                }),

            setHydrated: (hydrated) => set({ hydrated }),

            isAuthenticated: () => {
                const { accessToken, user } = get();
                return Boolean(accessToken && user);
            },

            isAdmin: () => get().user?.role === "ADMIN",
            isTeacher: () => get().user?.role === "TEACHER",
            isStudent: () => get().user?.role === "STUDENT",
        }),
        {
            name: "vidyaai.auth",
            storage: createJSONStorage(() => localStorage),
            partialize: (state) => ({
                accessToken: state.accessToken,
                refreshToken: state.refreshToken,
                user: state.user,
            }),
            onRehydrateStorage: () => (state, error) => {
                // Always mark hydrated after rehydration attempt (success or failure).
                if (error) state?.clear();
                state?.setHydrated(true);
            },
        },
    ),
);
