import type { VideoStatus } from "@/types/video";

type BadgeVariant =
  "default" | "success" | "warning" | "danger" | "info" | "muted";

interface StatusMeta {
  label: string;
  variant: BadgeVariant;
}

export function getVideoStatusMeta(status: VideoStatus): StatusMeta {
  switch (status) {
    case "PENDING":
      return { label: "Pending", variant: "muted" };
    case "UPLOADING":
      return { label: "Uploading", variant: "info" };
    case "UPLOADED":
      return { label: "Uploaded", variant: "info" };
    case "PROCESSING":
      return { label: "Preparing", variant: "warning" };
    case "COMPLETED":
      return { label: "Ready", variant: "success" };
    case "FAILED":
      return { label: "Needs attention", variant: "danger" };
    case "DELETED":
      return { label: "Deleted", variant: "muted" };
    default:
      return { label: String(status), variant: "default" };
  }
}

export function isProcessing(status: VideoStatus): boolean {
  return (
    status === "PROCESSING" ||
    status === "UPLOADING" ||
    status === "PENDING" ||
    status === "UPLOADED"
  );
}
