import Link from "next/link";
import { RegisterForm } from "@/components/auth/register-form";
import { AuthFrame } from "@/components/auth/auth-frame";
export const metadata = { title: "Create account" };
export default function Page() {
  return (
    <AuthFrame
      title="Start your study space."
      description="Bring your lectures, find the key ideas, and learn at your own pace."
      footer={
        <>
          Already have an account?{" "}
          <Link
            href="/login"
            className="text-accent underline underline-offset-4"
          >
            Sign in
          </Link>
        </>
      }
    >
      <RegisterForm />
      <p className="mt-5 text-xs leading-6 text-text-tertiary">
        By continuing, read our{" "}
        <Link href="/terms" className="underline">
          terms
        </Link>{" "}
        and{" "}
        <Link href="/privacy" className="underline">
          privacy information
        </Link>
        .
      </p>
    </AuthFrame>
  );
}
