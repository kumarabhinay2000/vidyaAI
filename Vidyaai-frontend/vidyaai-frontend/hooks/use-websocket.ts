"use client";

import { useEffect, useMemo, useRef, useState } from "react";

import { WebSocketClient, type ConnectionStatus } from "@/lib/websocket/client";
import { WS_URL } from "@/lib/constants";

type MessageHandler = (payload: unknown) => void;

interface Subscription {
  type: string;
  handler: MessageHandler;
}

export function useWebSocket(subscriptions: Subscription[] = []) {
  const [status, setStatus] = useState<ConnectionStatus>("idle");
  const clientRef = useRef<WebSocketClient | null>(null);

  // Stable string signature so subscriptions prop doesn't trigger reconnects.
  const signature = useMemo(
    () => subscriptions.map((s) => s.type).join("|"),
    [subscriptions],
  );

  useEffect(() => {
    const client = new WebSocketClient({
      url: WS_URL,
      autoReconnect: true,
      maxReconnectAttempts: 5,
    });
    clientRef.current = client;

    const unsubStatus = client.subscribeStatus(setStatus);

    const unsubscribers = subscriptions.map((sub) =>
      client.subscribe(sub.type, sub.handler),
    );

    client.connect();

    return () => {
      unsubscribers.forEach((u) => u());
      unsubStatus();
      client.close();
      clientRef.current = null;
    };
    // Re-run only when the shape of subscribed types changes.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [signature]);

  return {
    status,
    send: (type: string, payload?: unknown) =>
      clientRef.current?.send(type, payload),
    getClient: () => clientRef.current,
  };
}
