"use client";

import { use } from "react";

import { ChatWindow } from "@/components/ai/chat-window";
import { SectionCard } from "@/components/ai/section-card";

interface PageProps {
  params: Promise<{ videoId: string }>;
}

export default function VideoChatPage({ params }: PageProps) {
  const { videoId } = use(params);

  return (
    <div className="space-y-6">
      <SectionCard
        title="AI Tutor"
        description="Ask questions about this video. Answers use the video content only."
      >
        <ChatWindow videoId={videoId} />
      </SectionCard>
    </div>
  );
}
