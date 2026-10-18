import { LessonWorkspace } from "@/components/videos/lesson-workspace";
export default async function Layout({
  children,
  params,
}: {
  children: React.ReactNode;
  params: Promise<{ videoId: string }>;
}) {
  const { videoId } = await params;
  return <LessonWorkspace videoId={videoId}>{children}</LessonWorkspace>;
}
