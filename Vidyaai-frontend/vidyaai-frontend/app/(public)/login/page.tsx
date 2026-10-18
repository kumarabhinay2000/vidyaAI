import Link from "next/link";
import { Suspense } from "react";
import { LoginForm } from "@/components/auth/login-form";
import { AuthFrame } from "@/components/auth/auth-frame";
export const metadata = { title: "Sign in" };
export default function Page() {
  return (
    <AuthFrame
      title="Welcome back."
      description="Pick up your notes, revisit an idea, and keep learning."
      footer={
        <>
          New here?{" "}
          <Link
            href="/register"
            className="text-accent underline underline-offset-4"
          >
            Create your study space
          </Link>
        </>
      }
    >
      <Suspense fallback={<p role="status">Loading sign-in form…</p>}>
        <LoginForm />
      </Suspense>
    </AuthFrame>
  );
}
