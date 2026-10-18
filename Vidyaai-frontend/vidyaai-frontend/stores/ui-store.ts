import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";

interface UIState {
    sidebarCollapsed: boolean;
    commandPaletteOpen: boolean;
    toggleSidebar: () => void;
    setSidebarCollapsed: (collapsed: boolean) => void;
    openCommandPalette: () => void;
    closeCommandPalette: () => void;
    toggleCommandPalette: () => void;
}

export const useUIStore = create<UIState>()(
    persist(
        (set) => ({
            sidebarCollapsed: false,
            commandPaletteOpen: false,

            toggleSidebar: () =>
                set((s) => ({ sidebarCollapsed: !s.sidebarCollapsed })),

            setSidebarCollapsed: (collapsed) =>
                set({ sidebarCollapsed: collapsed }),

            openCommandPalette: () => set({ commandPaletteOpen: true }),
            closeCommandPalette: () => set({ commandPaletteOpen: false }),
            toggleCommandPalette: () =>
                set((s) => ({ commandPaletteOpen: !s.commandPaletteOpen })),
        }),
        {
            name: "vidyaai.ui",
            storage: createJSONStorage(() => localStorage),
            partialize: (state) => ({
                sidebarCollapsed: state.sidebarCollapsed,
            }),
        },
    ),
);