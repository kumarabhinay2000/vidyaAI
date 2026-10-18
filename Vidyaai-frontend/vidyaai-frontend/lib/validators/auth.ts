import { z } from "zod";

export const emailSchema = z
    .string()
    .min(1, "Email is required")
    .email("Enter a valid email address")
    .max(255, "Email must not exceed 255 characters");

export const passwordSchema = z
    .string()
    .min(8, "Password must be at least 8 characters")
    .max(100, "Password must not exceed 100 characters")
    .regex(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
        "Must contain uppercase, lowercase, digit, and special character",
    );

export const loginSchema = z.object({
    email: emailSchema,
    password: z.string().min(1, "Password is required"),
});

export const registerSchema = z
    .object({
        firstName: z
            .string()
            .min(2, "First name must be at least 2 characters")
            .max(100, "First name must not exceed 100 characters")
            .regex(/^[a-zA-Z\s]+$/, "Only letters and spaces allowed"),
        lastName: z
            .string()
            .max(100, "Last name must not exceed 100 characters")
            .regex(/^[a-zA-Z\s]*$/, "Only letters and spaces allowed")
            .optional()
            .or(z.literal("")),
        email: emailSchema,
        password: passwordSchema,
        confirmPassword: z.string().min(1, "Confirm your password"),
    })
    .refine((data) => data.password === data.confirmPassword, {
        path: ["confirmPassword"],
        message: "Passwords do not match",
    });

export const forgotPasswordSchema = z.object({
    email: emailSchema,
});

export type LoginFormValues = z.infer<typeof loginSchema>;
export type RegisterFormValues = z.infer<typeof registerSchema>;
export type ForgotPasswordFormValues = z.infer<typeof forgotPasswordSchema>;