"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { useQueryClient } from "@tanstack/react-query";
import { authApi } from "@/lib/api/auth";
import { useAuthStore } from "@/stores/auth-store";
import { ConfirmDialog } from "@/components/common/confirm-dialog";
export function DangerZone() {
  const [action, setAction] = useState<"sessions" | "delete" | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const router = useRouter();
  const cache = useQueryClient();
  async function confirm() {
    setBusy(true);
    setError("");
    try {
      if (action === "delete") await authApi.deleteAccount();
      else await authApi.logoutAllDevices();
      useAuthStore.getState().clear();
      cache.clear();
      router.replace("/login");
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "This action could not be completed.",
      );
    } finally {
      setBusy(false);
      setAction(null);
    }
  }
  return (
    <section className="border-t border-border pt-7">
      <h2 className="text-lg font-medium">Account actions</h2>
      <div className="mt-5 flex flex-wrap items-center justify-between gap-4 border-b border-border py-5">
        <div>
          <h3 className="font-medium">Sign out of all devices</h3>
          <p className="mt-2 text-sm text-text-secondary">
            You will need to sign in again.
          </p>
        </div>
        <button
          className="quiet-link rounded border border-border"
          onClick={() => setAction("sessions")}
        >
          Sign out all
        </button>
      </div>
      <div className="flex flex-wrap items-center justify-between gap-4 py-5">
        <div>
          <h3 className="font-medium text-danger">Delete account</h3>
          <p className="mt-2 text-sm text-text-secondary">
            Deactivate your account and revoke its sessions.
          </p>
        </div>
        <button
          className="quiet-link rounded border border-danger/30 text-danger"
          onClick={() => setAction("delete")}
        >
          Delete account
        </button>
      </div>
      {error && (
        <p role="alert" className="text-sm text-danger">
          {error}
        </p>
      )}
      <ConfirmDialog
        open={action !== null}
        onOpenChange={(open) => {
          if (!open && !busy) setAction(null);
        }}
        title={
          action === "delete"
            ? "Delete your account?"
            : "Sign out of all devices?"
        }
        description={
          action === "delete"
            ? "Your account will be deactivated. You will lose access to your learning workspace."
            : "You will be signed out and need to sign in again."
        }
        destructive={action === "delete"}
        confirmLabel={
          action === "delete" ? "Delete my account" : "Sign out all"
        }
        loading={busy}
        onConfirm={confirm}
      />
    </section>
  );
}
