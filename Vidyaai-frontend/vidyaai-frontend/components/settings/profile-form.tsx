"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { z } from "zod";
import { useEffect } from "react";

import { Button } from "@/components/ui/button";
import { FieldError } from "@/components/ui/field-error";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useUpdateProfile } from "@/hooks/use-auth";
import { useAuthStore } from "@/stores/auth-store";

const schema = z.object({
    firstName: z
        .string()
        .min(2, "First name must be at least 2 characters")
        .max(100, "Too long")
        .regex(/^[a-zA-Z\s]+$/, "Only letters and spaces allowed"),
    lastName: z
        .string()
        .max(100, "Too long")
        .regex(/^[a-zA-Z\s]*$/, "Only letters and spaces allowed")
        .optional()
        .or(z.literal("")),
    profilePicUrl: z
        .string()
        .url("Enter a valid URL")
        .optional()
        .or(z.literal("")),
});

type Values = z.infer<typeof schema>;

export function ProfileForm() {
    const user = useAuthStore((s) => s.user);
    const mutation = useUpdateProfile();
    const form = useForm<Values>({
        resolver: zodResolver(schema),
        defaultValues: {
            firstName: user?.firstName ?? "",
            lastName: user?.lastName ?? "",
            profilePicUrl: user?.profilePicUrl ?? "",
        },
    });

    useEffect(() => {
        form.reset({
            firstName: user?.firstName ?? "",
            lastName: user?.lastName ?? "",
            profilePicUrl: user?.profilePicUrl ?? "",
        });
    }, [user, form]);

    const onSubmit = form.handleSubmit((values) => {
        mutation.mutate({
            firstName: values.firstName.trim(),
            lastName: values.lastName?.trim() || undefined,
            profilePicUrl: values.profilePicUrl?.trim() || undefined,
        });
    });

    const pending = mutation.isPending;

    return (
        <form onSubmit={onSubmit} noValidate className="space-y-4">
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                    <Label htmlFor="firstName" className="text-xs text-text-secondary">
                        First name
                    </Label>
                    <Input
                        id="firstName"
                        disabled={pending}
                        className="mt-1.5 h-9 border-border bg-surface-2 text-sm focus-visible:border-accent focus-visible:ring-0"
                        {...form.register("firstName")}
                    />
                    <FieldError message={form.formState.errors.firstName?.message} />
                </div>

                <div>
                    <Label htmlFor="lastName" className="text-xs text-text-secondary">
                        Last name
                    </Label>
                    <Input
                        id="lastName"
                        disabled={pending}
                        className="mt-1.5 h-9 border-border bg-surface-2 text-sm focus-visible:border-accent focus-visible:ring-0"
                        {...form.register("lastName")}
                    />
                    <FieldError message={form.formState.errors.lastName?.message} />
                </div>
            </div>

            <div>
                <Label htmlFor="profilePicUrl" className="text-xs text-text-secondary">
                    Profile picture URL <span className="text-text-tertiary">(optional)</span>
                </Label>
                <Input
                    id="profilePicUrl"
                    placeholder="https://…"
                    disabled={pending}
                    className="mt-1.5 h-9 border-border bg-surface-2 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
                    {...form.register("profilePicUrl")}
                />
                <FieldError message={form.formState.errors.profilePicUrl?.message} />
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
                            Saving…
                        </>
                    ) : (
                        "Save changes"
                    )}
                </Button>
            </div>
        </form>
    );
}
