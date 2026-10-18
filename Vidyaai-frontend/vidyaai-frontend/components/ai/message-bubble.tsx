"use client";

import { BookOpen, User } from "lucide-react";

import { cn } from "@/lib/utils";
import type { ChatMessage } from "@/types/ai";

interface MessageBubbleProps {
  message: ChatMessage;
  timestamp?: Date;
}

export function MessageBubble({ message, timestamp }: MessageBubbleProps) {
  const isUser = message.role === "user";
  const Icon = isUser ? User : BookOpen;

  return (
    <div className={cn("flex gap-3", isUser ? "justify-end" : "justify-start")}>
      {!isUser ? (
        <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-md bg-surface-2">
          <Icon className="h-3.5 w-3.5 text-accent" />
        </span>
      ) : null}

      <div
        className={cn(
          "max-w-[85%] rounded-lg border px-3 py-2 text-sm leading-relaxed",
          isUser
            ? "border-accent/30 bg-accent-muted text-text-primary"
            : "border-border bg-surface-2 text-text-secondary",
        )}
      >
        <p className="whitespace-pre-wrap">{message.content}</p>
        {timestamp ? (
          <p className="mt-1 text-[10px] text-text-tertiary">
            {timestamp.toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            })}
          </p>
        ) : null}
      </div>

      {isUser ? (
        <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-md bg-surface-2">
          <Icon className="h-3.5 w-3.5 text-text-secondary" />
        </span>
      ) : null}
    </div>
  );
}
