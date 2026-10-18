export type UserRole = "STUDENT" | "TEACHER" | "ADMIN";
export type UserStatus = "ACTIVE" | "INACTIVE" | "SUSPENDED" | "DELETED";
export type PlanType = "FREE" | "PRO" | "TEAM" | "ENTERPRISE";

export interface User {
    id: string;
    email: string;
    firstName: string;
    lastName?: string | null;
    fullName?: string | null;
    role: UserRole;
    status: UserStatus;
    planType: PlanType;
    profilePicUrl?: string | null;
    emailVerified: boolean;
    lastLogin?: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface AuthSession {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    user: User;
}

export interface LoginPayload {
    email: string;
    password: string;
}

export interface RegisterPayload {
    email: string;
    password: string;
    firstName: string;
    lastName?: string;
}

export interface ChangePasswordPayload {
    currentPassword: string;
    newPassword: string;
}

export interface UpdateProfilePayload {
    firstName?: string;
    lastName?: string;
    profilePicUrl?: string;
}