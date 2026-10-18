"use client";
import Link from "next/link";
export function ForgotPasswordForm() {
  return (
    <div className="rounded-lg border border-border bg-surface p-5">
      <p className="text-sm font-medium">
        Password recovery is not available yet.
      </p>
      <p className="mt-3 text-sm leading-7 text-text-secondary">
        Reset emails cannot be sent by this version. You can return to sign in
        or contact your platform administrator for help.
      </p>
      <Link href="/login" className="primary-link mt-5 text-sm">
        Back to sign in
      </Link>
    </div>
  );
}
