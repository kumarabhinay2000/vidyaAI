"use client";

import { useMemo } from "react";
import { WifiOff } from "lucide-react";

import { useWebSocket } from "@/hooks/use-websocket";

export function NetworkStatusBanner() {
    const subscriptions = useMemo(() => [], []);
    const { status } = useWebSocket(subscriptions);

    if (status === "connected" || status === "connecting" || status === "idle") {
        return null;
    }

    return (
        <div className="border-b border-warning/30 bg-warning-muted px-4 py-1.5 text-center text-[11px] text-warning">
      <span className="inline-flex items-center gap-1.5">
        <WifiOff className="h-3 w-3" />
        Live updates paused. Reconnecting…
      </span>
        </div>
    );
}