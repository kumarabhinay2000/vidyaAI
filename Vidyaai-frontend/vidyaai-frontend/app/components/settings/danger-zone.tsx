"use client";

import { useState } from "react";
import { LogOut, ShieldAlert } from "lucide-react";

import { ConfirmDialog } from "@/components/common/confirm-dialog";
import { Button } from "@/components/ui/button";
import { authApi } from "@/lib/api/auth";
import { isApiError } from "@/lib/api";
import { useAuthStore } from "@/stores/auth-store";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

export function DangerZone() {
  const router = useRouter();
  const clear = useAuthStore((s) => s.clear);
  const [confirmLogoutAll, setConfirmLogoutAll] = useState(false);
  const [busy, setBusy] = useState(false);

  const handleLogoutAll = async () => {
    setBusy(true);
    try {
      await authApi.logoutAllDevices();
      toast.success("Signed out from all devices");
      clear();
      router.replace("/login");
    } catch (err) {
      toast.error(isApiError(err) ? err.message : "Could not complete request");
    } finally {
      setBusy(false);
      setConfirmLogoutAll(false);
    }
  };

  return (
    <div className="rounded-lg border border-danger/30 bg-danger-muted/30">
      <div className="flex items-start gap-3 border-b border-danger/20 px-4 py-3">
        <ShieldAlert className="mt-0.5 h-4 w-4 text-danger" />
        <div>
          <p className="text-sm font-medium text-text-primary">Danger zone</p>
          <p className="mt-0.5 text-[11px] text-text-secondary">
            Actions that affect your active sessions.
          </p>
        </div>
      </div>

      <div className="space-y-4 px-4 py-4">
        <div className="flex items-start justify-between gap-4">
          <div>
            <p className="text-sm text-text-primary">Sign out of all devices</p>
            <p className="mt-0.5 text-[11px] text-text-secondary">
              All refresh tokens will be revoked. You will need to sign in
              again.
            </p>
          </div>
          <Button
            type="button"
            variant="outline"
            onClick={() => setConfirmLogoutAll(true)}
            className="h-8 border-danger/40 bg-transparent text-xs text-danger hover:bg-danger-muted hover:text-danger"
          >
            <LogOut className="mr-1.5 h-3.5 w-3.5" />
            Sign out all
          </Button>
        </div>
      </div>

      <ConfirmDialog
        open={confirmLogoutAll}
        onOpenChange={setConfirmLogoutAll}
        title="Sign out of all devices?"
        description="All active sessions will be terminated. You will be redirected to sign in again."
        confirmLabel="Sign out all"
        destructive
        loading={busy}
        onConfirm={handleLogoutAll}
      />
    </div>
  );
}
