"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";

import { FieldError } from "@/components/ui/field-error";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useRegister } from "@/hooks/use-auth";
import { registerSchema, type RegisterFormValues } from "@/lib/validators/auth";

export function RegisterForm() {
  const mutation = useRegister();

  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      confirmPassword: "",
    },
    mode: "onSubmit",
  });

  const onSubmit = form.handleSubmit((values) => {
    mutation.mutate({
      firstName: values.firstName.trim(),
      lastName: values.lastName?.trim() || undefined,
      email: values.email.trim().toLowerCase(),
      password: values.password,
    });
  });

  const pending = mutation.isPending;

  return (
    <form onSubmit={onSubmit} noValidate className="space-y-4">
      <div>
        <Label htmlFor="firstName" className="text-xs text-text-secondary">
          First name
        </Label>
        <Input
          id="firstName"
          autoComplete="given-name"
          placeholder="Rahul"
          disabled={pending}
          aria-invalid={Boolean(form.formState.errors.firstName) || undefined}
          className="mt-1.5 h-9 border-border bg-surface-2 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
          {...form.register("firstName")}
        />
        <FieldError message={form.formState.errors.firstName?.message} />
      </div>

      <div>
        <Label htmlFor="lastName" className="text-xs text-text-secondary">
          Last name <span className="text-text-tertiary">(optional)</span>
        </Label>
        <Input
          id="lastName"
          autoComplete="family-name"
          placeholder="Sharma"
          disabled={pending}
          aria-invalid={Boolean(form.formState.errors.lastName) || undefined}
          className="mt-1.5 h-9 border-border bg-surface-2 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
          {...form.register("lastName")}
        />
        <FieldError message={form.formState.errors.lastName?.message} />
      </div>

      <div>
        <Label htmlFor="email" className="text-xs text-text-secondary">
          Email
        </Label>
        <Input
          id="email"
          type="email"
          autoComplete="email"
          placeholder="you@example.com"
          disabled={pending}
          aria-invalid={Boolean(form.formState.errors.email) || undefined}
          className="mt-1.5 h-9 border-border bg-surface-2 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
          {...form.register("email")}
        />
        <FieldError message={form.formState.errors.email?.message} />
      </div>

      <div>
        <Label htmlFor="password" className="text-xs text-text-secondary">
          Password
        </Label>
        <Input
          id="password"
          type="password"
          autoComplete="new-password"
          placeholder="At least 8 characters"
          disabled={pending}
          aria-invalid={Boolean(form.formState.errors.password) || undefined}
          className="mt-1.5 h-9 border-border bg-surface-2 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
          {...form.register("password")}
        />
        <FieldError message={form.formState.errors.password?.message} />
        <p className="mt-1 text-[11px] text-text-tertiary">
          Must include uppercase, lowercase, digit, and a special character.
        </p>
      </div>

      <div>
        <Label
          htmlFor="confirmPassword"
          className="text-xs text-text-secondary"
        >
          Confirm password
        </Label>
        <Input
          id="confirmPassword"
          type="password"
          autoComplete="new-password"
          placeholder="Re-enter password"
          disabled={pending}
          aria-invalid={
            Boolean(form.formState.errors.confirmPassword) || undefined
          }
          className="mt-1.5 h-9 border-border bg-surface-2 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
          {...form.register("confirmPassword")}
        />
        <FieldError message={form.formState.errors.confirmPassword?.message} />
      </div>

      <Button
        type="submit"
        disabled={pending}
        className="mt-2 h-9 w-full bg-accent text-sm font-medium text-accent-fg transition-colors hover:bg-accent-hover"
      >
        {pending ? (
          <>
            <Loader2 className="mr-2 h-3.5 w-3.5 animate-spin" />
            Creating account…
          </>
        ) : (
          "Create account"
        )}
      </Button>
    </form>
  );
}
