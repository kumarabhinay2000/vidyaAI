"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { z } from "zod";

import { Button } from "@/components/ui/button";
import { FieldError } from "@/components/ui/field-error";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useChangePassword } from "@/hooks/use-auth";
import { passwordSchema } from "@/lib/validators/auth";

const schema = z
    .object({
        currentPassword: z.string().min(1, "Current password is required"),
        newPassword: passwordSchema,
        confirmPassword: z.string().min(1, "Confirm your new password"),
    })
    .refine((d) => d.newPassword === d.confirmPassword, {
        path: ["confirmPassword"],
        message: "Passwords do not match",
    });

type Values = z.infer<typeof schema>;

export function ChangePasswordForm() {
    const mutation = useChangePassword();
    const form = useForm<Values>({
        resolver: zodResolver(schema),
        defaultValues: {
            currentPassword: "",
            newPassword: "",
            confirmPassword: "",
        },
    });

    const onSubmit = form.handleSubmit((values) => {
        mutation.mutate(
            {
                currentPassword: values.currentPassword,
                newPassword: values.newPassword,
            },
            {
                onSuccess: () => form.reset(),
            },
        );
    });

    const pending = mutation.isPending;

    return (
        <form onSubmit={onSubmit} noValidate className="space-y-4">
            <div>
                <Label htmlFor="currentPassword" className="text-xs text-text-secondary">
                    Current password
                </Label>
                <Input
                    id="currentPassword"
                    type="password"
                    autoComplete="current-password"
                    disabled={pending}
                    className="mt-1.5 h-9 border-border bg-surface-2 text-sm focus-visible:border-accent focus-visible:ring-0"
                    {...form.register("currentPassword")}
                />
                <FieldError message={form.formState.errors.currentPassword?.message} />
            </div>

            <div>
                <Label htmlFor="newPassword" className="text-xs text-text-secondary">
                    New password
                </Label>
                <Input
                    id="newPassword"
                    type="password"
                    autoComplete="new-password"
                    disabled={pending}
                    className="mt-1.5 h-9 border-border bg-surface-2 text-sm focus-visible:border-accent focus-visible:ring-0"
                    {...form.register("newPassword")}
                />
                <FieldError message={form.formState.errors.newPassword?.message} />
                <p className="mt-1 text-[11px] text-text-tertiary">
                    Use at least 8 characters with uppercase, lowercase, digit, and a symbol.
                </p>
            </div>

            <div>
                <Label htmlFor="confirmPassword" className="text-xs text-text-secondary">
                    Confirm new password
                </Label>
                <Input
                    id="confirmPassword"
                    type="password"
                    autoComplete="new-password"
                    disabled={pending}
                    className="mt-1.5 h-9 border-border bg-surface-2 text-sm focus-visible:border-accent focus-visible:ring-0"
                    {...form.register("confirmPassword")}
                />
                <FieldError message={form.formState.errors.confirmPassword?.message} />
            </div>

            <div className="flex justify-end pt-2">
                <Button
                    type="submit"
                    disabled={pending}
                    className="h-9 bg-accent px-4 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
                >
                    {pending ? (
                        <>
                            <Loader2 className="mr-2 h-3.5 w-3.5 animate-spin" />
                            Updating…
                        </>
                    ) : (
                        "Update password"
                    )}
                </Button>
            </div>
        </form>
    );
}
