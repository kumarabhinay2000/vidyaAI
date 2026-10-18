"use client";

import { useEffect, useMemo } from "react";
import { toast } from "sonner";

import { useAuthStore } from "@/stores/auth-store";
import { useWebSocket } from "@/hooks/use-websocket";

interface NotificationPayload {
  title: string;
  body?: string;
  variant?: "info" | "success" | "warning" | "error";
}

function ConnectedNotifications({ children }: { children: React.ReactNode }) {
  const subscriptions = useMemo(
    () => [
      {
        type: "notification",
        handler: (payload: unknown) => {
          const p = payload as NotificationPayload;
          if (!p?.title) return;
          const fn =
            p.variant === "success"
              ? toast.success
              : p.variant === "error"
                ? toast.error
                : p.variant === "warning"
                  ? toast.warning
                  : toast;
          fn(p.title, { description: p.body });
        },
      },
      {
        type: "video.status",
        handler: (payload: unknown) => {
          const p = payload as {
            videoId: string;
            status: string;
            progress: number;
          };
          if (p.status === "COMPLETED") {
            toast.success("Video processing complete", {
              description: "Summary, notes, and quiz are ready.",
            });
          } else if (p.status === "FAILED") {
            toast.error("Video processing failed", {
              description: "Please retry from the video page.",
            });
          }
        },
      },
    ],
    [],
  );

  // Open a single connection at the app root.
  useEffect(() => {
    const { client } = { client: null } as { client: null };
    // The hook manages its own connection; nothing to do here.
    return () => {
      void client;
    };
  }, []);

  useWebSocket(subscriptions);

  return <>{children}</>;
}
export function NotificationStreamProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const user = useAuthStore((s) => s.user);
  return user ? (
    <ConnectedNotifications>{children}</ConnectedNotifications>
  ) : (
    <>{children}</>
  );
}
