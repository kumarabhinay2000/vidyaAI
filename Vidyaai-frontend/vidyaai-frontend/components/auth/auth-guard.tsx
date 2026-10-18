"use client";
import { useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuthStore } from "@/stores/auth-store";
export function AuthGuard({
  children,
  requireAdmin = false,
}: {
  children: React.ReactNode;
  requireAdmin?: boolean;
}) {
  const router = useRouter();
  const pathname = usePathname();
  const user = useAuthStore((s) => s.user);
  const token = useAuthStore((s) => s.accessToken);
  const hydrated = useAuthStore((s) => s.hydrated);
  useEffect(() => {
    if (!hydrated) return;
    if (!user || !token)
      router.replace("/login?next=" + encodeURIComponent(pathname));
    else if (requireAdmin && user.role !== "ADMIN")
      router.replace("/unauthorized");
  }, [hydrated, user, token, requireAdmin, pathname, router]);
  if (!hydrated || !user || !token || (requireAdmin && user.role !== "ADMIN"))
    return (
      <div
        className="flex min-h-screen items-center justify-center"
        role="status"
      >
        Opening your study space…
      </div>
    );
  return <>{children}</>;
}
