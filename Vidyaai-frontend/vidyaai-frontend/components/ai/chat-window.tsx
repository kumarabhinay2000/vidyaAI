"use client";

import { useEffect, useRef, useState } from "react";
import { Loader2, Send } from "lucide-react";

import { MessageBubble } from "./message-bubble";
import { Button } from "@/components/ui/button";
import { useAskChatbot } from "@/hooks/use-ai";
import type { ChatMessage } from "@/types/ai";

interface ChatWindowProps {
  videoId: string;
  sessionId?: string;
}

interface LocalMessage extends ChatMessage {
  sources?: string[];
  timestamp: Date;
}

export function ChatWindow({ videoId, sessionId }: ChatWindowProps) {
  const mutation = useAskChatbot();
  const [messages, setMessages] = useState<LocalMessage[]>([
    {
      role: "assistant",
      content:
        "Ask me anything about this video. I will answer using its content only.",
      timestamp: new Date(),
    },
  ]);
  const [input, setInput] = useState("");
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    scrollRef.current?.scrollTo({
      top: scrollRef.current.scrollHeight,
      behavior: "smooth",
    });
  }, [messages, mutation.isPending]);

  const send = () => {
    const question = input.trim();
    if (!question || mutation.isPending) return;

    const userMsg: LocalMessage = {
      role: "user",
      content: question,
      timestamp: new Date(),
    };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");

    const history = messages.map((m) => ({
      role: m.role,
      content: m.content,
    }));

    mutation.mutate(
      {
        videoId,
        question,
        sessionId,
        chatHistory: history,
        language: "ENGLISH",
      },
      {
        onError: () => {
          setInput(question);
        },
        onSuccess: (res) => {
          setMessages((prev) => [
            ...prev,
            {
              role: "assistant",
              content: res.answer,
              sources: res.sources,
              timestamp: new Date(),
            },
          ]);
        },
      },
    );
  };

  const onKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      send();
    }
  };

  return (
    <div className="flex flex-col gap-4">
      <div
        ref={scrollRef}
        className="max-h-[60vh] min-h-[320px] space-y-4 overflow-y-auto rounded-lg border border-border bg-surface/40 p-4 scrollbar-thin"
      >
        {messages.map((m, idx) => (
          <div key={idx}>
            <MessageBubble message={m} timestamp={m.timestamp} />
            {m.sources?.length ? (
              <div className="mt-3 border-l-2 border-border pl-4">
                <p className="eyebrow">Retrieved lecture context</p>
                {m.sources.map((source, i) => (
                  <p
                    key={i}
                    className="mt-2 text-xs leading-6 text-text-secondary"
                  >
                    {source}
                  </p>
                ))}
              </div>
            ) : null}
          </div>
        ))}

        {mutation.isPending ? (
          <div className="flex items-center gap-2 text-xs text-text-tertiary">
            <Loader2 className="h-3.5 w-3.5 animate-spin" />
            <span>VidyaAI is thinking…</span>
          </div>
        ) : null}
      </div>

      {mutation.isError && (
        <p
          role="alert"
          className="rounded-md border border-danger/30 p-3 text-sm text-danger"
        >
          Your question could not be sent. It is restored below; try sending
          again.
        </p>
      )}
      <div className="flex items-end gap-2">
        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={onKeyDown}
          rows={2}
          disabled={mutation.isPending}
          placeholder="Ask a question about this video…"
          className="min-h-[44px] flex-1 resize-none rounded-md border border-border bg-surface-2 px-3 py-2 text-sm text-text-primary placeholder:text-text-tertiary focus:border-accent focus:outline-none disabled:opacity-60"
        />
        <Button
          type="button"
          disabled={!input.trim() || mutation.isPending}
          aria-label="Send question"
          onClick={send}
          className="h-9 shrink-0 bg-accent px-3 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover disabled:opacity-50"
        >
          <Send className="h-3.5 w-3.5" />
        </Button>
      </div>

      <p className="text-[10px] text-text-tertiary">
        Press Enter to send · Shift + Enter for a new line
      </p>
    </div>
  );
}
