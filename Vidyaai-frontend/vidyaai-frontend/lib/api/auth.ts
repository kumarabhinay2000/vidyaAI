import { apiRequest } from "./client";
import type {
  AuthSession,
  ChangePasswordPayload,
  LoginPayload,
  RegisterPayload,
  UpdateProfilePayload,
  User,
} from "@/types/user";

export const authApi = {
  async deleteAccount(): Promise<void> {
    await apiRequest<void>({ url: "/users/profile", method: "DELETE" });
  },
  async register(payload: RegisterPayload): Promise<AuthSession> {
    return await apiRequest<AuthSession>({
      url: "/auth/register",
      method: "POST",
      data: payload,
    });
  },

  async login(payload: LoginPayload): Promise<AuthSession> {
    return await apiRequest<AuthSession>({
      url: "/auth/login",
      method: "POST",
      data: payload,
    });
  },

  async refresh(): Promise<AuthSession> {
    const { useAuthStore } = await import("@/stores/auth-store");
    const state = useAuthStore.getState();
    const refreshToken = state.refreshToken;
    if (!refreshToken) throw new Error("No refresh token available");

    return await apiRequest<AuthSession>({
      url: "/auth/refresh-token",
      method: "POST",
      data: { refreshToken },
    });
  },

  async logout(): Promise<void> {
    const { useAuthStore } = await import("@/stores/auth-store");
    const refreshToken = useAuthStore.getState().refreshToken;
    if (!refreshToken) return;

    try {
      await apiRequest<void>({
        url: "/auth/logout",
        method: "POST",
        data: { refreshToken },
      });
    } finally {
      useAuthStore.getState().clear();
    }
  },

  async me(): Promise<User> {
    return await apiRequest<User>({
      url: "/users/profile",
      method: "GET",
    });
  },

  async updateProfile(payload: UpdateProfilePayload): Promise<User> {
    return await apiRequest<User>({
      url: "/users/profile",
      method: "PUT",
      data: payload,
    });
  },

  async changePassword(payload: ChangePasswordPayload): Promise<void> {
    await apiRequest<void>({
      url: "/users/change-password",
      method: "PUT",
      data: payload,
    });
  },

  async logoutAllDevices(): Promise<void> {
    await apiRequest<void>({
      url: "/users/logout-all-devices",
      method: "POST",
    });
  },
};
