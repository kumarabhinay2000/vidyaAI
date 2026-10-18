"use client";
import {
  createContext,
  useContext,
  useEffect,
  useSyncExternalStore,
  type ReactNode,
} from "react";
export type Theme = "light" | "dark" | "system";
export type ResolvedTheme = "light" | "dark";
const key = "vidyaai_theme";
let memory: Theme = "dark";
function snapshot() {
  let theme: Theme = memory;
  try {
    const saved = localStorage.getItem(key);
    if (saved === "light" || saved === "dark" || saved === "system")
      theme = saved;
  } catch {}
  const resolved =
    theme === "system"
      ? matchMedia("(prefers-color-scheme: dark)").matches
        ? "dark"
        : "light"
      : theme;
  return theme + "|" + resolved;
}
function subscribe(fn: () => void) {
  const media = matchMedia("(prefers-color-scheme: dark)");
  window.addEventListener("storage", fn);
  window.addEventListener("vidya-theme", fn);
  media.addEventListener("change", fn);
  return () => {
    window.removeEventListener("storage", fn);
    window.removeEventListener("vidya-theme", fn);
    media.removeEventListener("change", fn);
  };
}
function setTheme(theme: Theme) {
  memory = theme;
  try {
    localStorage.setItem(key, theme);
  } catch {}
  window.dispatchEvent(new Event("vidya-theme"));
}
const ThemeContext = createContext({
  theme: "dark" as Theme,
  resolvedTheme: "dark" as ResolvedTheme,
  setTheme,
  toggleTheme: () => {},
});
export function ThemeProvider({ children }: { children: ReactNode }) {
  const state = useSyncExternalStore(subscribe, snapshot, () => "dark|dark");
  const [theme, resolved] = state.split("|") as [Theme, ResolvedTheme];
  useEffect(() => {
    document.documentElement.classList.toggle("dark", resolved === "dark");
    document.documentElement.style.colorScheme = resolved;
  }, [resolved]);
  return (
    <ThemeContext.Provider
      value={{
        theme,
        resolvedTheme: resolved,
        setTheme,
        toggleTheme: () => setTheme(resolved === "dark" ? "light" : "dark"),
      }}
    >
      {children}
    </ThemeContext.Provider>
  );
}
export const useTheme = () => useContext(ThemeContext);
