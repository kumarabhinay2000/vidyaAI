"use client";

type MessageHandler = (payload: unknown) => void;
type StatusHandler = (status: ConnectionStatus) => void;

export type ConnectionStatus =
    | "idle"
    | "connecting"
    | "connected"
    | "disconnected"
    | "error";

interface WebSocketClientOptions {
    url: string;
    autoReconnect?: boolean;
    maxReconnectAttempts?: number;
    baseReconnectDelayMs?: number;
}

export class WebSocketClient {
    private socket: WebSocket | null = null;
    private url: string;
    private handlers = new Map<string, Set<MessageHandler>>();
    private statusHandlers = new Set<StatusHandler>();
    private status: ConnectionStatus = "idle";
    private reconnectAttempts = 0;
    private readonly autoReconnect: boolean;
    private readonly maxReconnectAttempts: number;
    private readonly baseReconnectDelayMs: number;
    private manualClose = false;
    private reconnectTimer: ReturnType<typeof setTimeout> | null = null;

    constructor(options: WebSocketClientOptions) {
        this.url = options.url;
        this.autoReconnect = options.autoReconnect ?? true;
        this.maxReconnectAttempts = options.maxReconnectAttempts ?? 5;
        this.baseReconnectDelayMs = options.baseReconnectDelayMs ?? 1000;
    }

    connect(): void {
        if (
            this.socket &&
            (this.socket.readyState === WebSocket.OPEN ||
                this.socket.readyState === WebSocket.CONNECTING)
        ) {
            return;
        }

        this.manualClose = false;
        this.setStatus("connecting");

        try {
            this.socket = new WebSocket(this.url);

            this.socket.onopen = () => {
                this.reconnectAttempts = 0;
                this.setStatus("connected");
            };

            this.socket.onmessage = (event) => {
                try {
                    const parsed = JSON.parse(event.data) as {
                        type?: string;
                        payload?: unknown;
                    };
                    if (parsed?.type) {
                        this.dispatch(parsed.type, parsed.payload);
                    }
                } catch {
                    // Ignore malformed frames.
                }
            };

            this.socket.onerror = () => {
                this.setStatus("error");
            };

            this.socket.onclose = () => {
                this.socket = null;
                this.setStatus("disconnected");
                if (!this.manualClose && this.autoReconnect) {
                    this.scheduleReconnect();
                }
            };
        } catch {
            this.setStatus("error");
            this.scheduleReconnect();
        }
    }

    private scheduleReconnect(): void {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) return;
        this.reconnectAttempts += 1;
        const delay = this.baseReconnectDelayMs * 2 ** (this.reconnectAttempts - 1);
        this.reconnectTimer = setTimeout(() => this.connect(), delay);
    }

    private setStatus(next: ConnectionStatus): void {
        this.status = next;
        this.statusHandlers.forEach((h) => h(next));
    }

    private dispatch(type: string, payload: unknown): void {
        const handlers = this.handlers.get(type);
        if (!handlers) return;
        handlers.forEach((h) => {
            try {
                h(payload);
            } catch {
                // Individual handler errors should not break others.
            }
        });
    }

    subscribe(type: string, handler: MessageHandler): () => void {
        if (!this.handlers.has(type)) {
            this.handlers.set(type, new Set());
        }
        this.handlers.get(type)!.add(handler);
        return () => {
            this.handlers.get(type)?.delete(handler);
        };
    }

    subscribeStatus(handler: StatusHandler): () => void {
        this.statusHandlers.add(handler);
        handler(this.status);
        return () => {
            this.statusHandlers.delete(handler);
        };
    }

    send(type: string, payload?: unknown): void {
        if (!this.socket || this.socket.readyState !== WebSocket.OPEN) return;
        this.socket.send(JSON.stringify({ type, payload }));
    }

    close(): void {
        this.manualClose = true;
        if (this.reconnectTimer) {
            clearTimeout(this.reconnectTimer);
            this.reconnectTimer = null;
        }
        this.socket?.close();
        this.socket = null;
        this.setStatus("idle");
    }

    getStatus(): ConnectionStatus {
        return this.status;
    }
}