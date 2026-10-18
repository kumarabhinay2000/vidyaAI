"use client";

import { Loader2 } from "lucide-react";

import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { cn } from "@/lib/utils";

interface ConfirmDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    title: string;
    description: string;
    confirmLabel?: string;
    cancelLabel?: string;
    destructive?: boolean;
    loading?: boolean;
    onConfirm: () => void | Promise<void>;
}

export function ConfirmDialog({
                                  open,
                                  onOpenChange,
                                  title,
                                  description,
                                  confirmLabel = "Confirm",
                                  cancelLabel = "Cancel",
                                  destructive = false,
                                  loading = false,
                                  onConfirm,
                              }: ConfirmDialogProps) {
    return (
        <AlertDialog open={open} onOpenChange={onOpenChange}>
            <AlertDialogContent className="border-border bg-surface-2">
                <AlertDialogHeader>
                    <AlertDialogTitle className="text-text-primary">
                        {title}
                    </AlertDialogTitle>
                    <AlertDialogDescription className="text-text-secondary">
                        {description}
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel
                        disabled={loading}
                        className="border-border bg-transparent text-text-secondary hover:bg-surface-3 hover:text-text-primary"
                    >
                        {cancelLabel}
                    </AlertDialogCancel>
                    <AlertDialogAction
                        disabled={loading}
                        onClick={(e) => {
                            e.preventDefault();
                            void onConfirm();
                        }}
                        className={cn(
                            "text-accent-fg",
                            destructive
                                ? "bg-danger hover:bg-danger/90"
                                : "bg-accent hover:bg-accent-hover",
                        )}
                    >
                        {loading ? (
                            <Loader2 className="mr-2 h-3.5 w-3.5 animate-spin" />
                        ) : null}
                        {confirmLabel}
                    </AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}