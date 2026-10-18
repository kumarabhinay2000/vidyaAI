"use client";
import { useState } from "react";
import Link from "next/link";
import { Eye, EyeOff } from "lucide-react";
import { useLogin } from "@/hooks/use-auth";
export function LoginForm() {
  const mutation = useLogin();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [show, setShow] = useState(false);
  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        mutation.mutate({ email: email.trim().toLowerCase(), password });
      }}
      className="space-y-5"
    >
      <div>
        <label htmlFor="email" className="block text-sm font-medium">
          Email
        </label>
        <input
          id="email"
          type="email"
          required
          autoComplete="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="mt-2 w-full rounded-md border border-border bg-surface px-3"
          placeholder="you@example.com"
        />
      </div>
      <div>
        <label htmlFor="password" className="block text-sm font-medium">
          Password
        </label>
        <div className="relative mt-2">
          <input
            id="password"
            type={show ? "text" : "password"}
            required
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full rounded-md border border-border bg-surface pl-3 pr-12"
          />
          <button
            type="button"
            aria-label={show ? "Hide password" : "Show password"}
            onClick={() => setShow((s) => !s)}
            className="absolute right-0 top-0 grid h-11 w-11 place-items-center text-text-secondary"
          >
            {show ? (
              <EyeOff className="h-4 w-4" />
            ) : (
              <Eye className="h-4 w-4" />
            )}
          </button>
        </div>
        <Link
          href="/forgot-password"
          className="mt-2 inline-flex min-h-11 items-center text-sm text-text-secondary underline underline-offset-4"
        >
          Forgot password?
        </Link>
      </div>
      {mutation.isError && (
        <p
          role="alert"
          className="rounded-md border border-danger/30 p-3 text-sm text-danger"
        >
          {mutation.error.message ||
            "We couldn’t sign you in. Please try again."}
        </p>
      )}
      <button
        type="submit"
        disabled={mutation.isPending}
        className="primary-link w-full disabled:opacity-60"
      >
        {mutation.isPending ? "Signing in…" : "Sign in"}
      </button>
    </form>
  );
}
