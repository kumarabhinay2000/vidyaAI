"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter, useSearchParams } from "next/navigation";
import { toast } from "sonner";

import { authApi } from "@/lib/api/auth";
import { ApiError, isApiError } from "@/lib/api";
import { queryKeys } from "@/lib/query-keys";
import { useAuthStore } from "@/stores/auth-store";
import type {
  ChangePasswordPayload,
  LoginPayload,
  RegisterPayload,
  UpdateProfilePayload,
} from "@/types/user";

function getErrorMessage(error: unknown): string {
  if (isApiError(error)) return error.message;
  if (error instanceof Error) return error.message;
  return "Something went wrong. Please try again.";
}

function resolveRedirectAfterLogin(
  searchParams: URLSearchParams | null,
): string {
  const next = searchParams?.get("next");
  if (next && next.startsWith("/") && !next.startsWith("//")) return next;
  return "/dashboard";
}

export function useLogin() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const queryClient = useQueryClient();
  const setSession = useAuthStore((s) => s.setSession);

  return useMutation({
    mutationFn: (payload: LoginPayload) => authApi.login(payload),
    onSuccess: (session) => {
      setSession(session);
      queryClient.setQueryData(queryKeys.auth.me, session.user);
      toast.success(`Welcome back, ${session.user.firstName}`);
      router.replace(resolveRedirectAfterLogin(searchParams));
    },
    onError: (error) => {
      toast.error(getErrorMessage(error));
    },
  });
}

export function useRegister() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const setSession = useAuthStore((s) => s.setSession);

  return useMutation({
    mutationFn: (payload: RegisterPayload) => authApi.register(payload),
    onSuccess: (session) => {
      setSession(session);
      queryClient.setQueryData(queryKeys.auth.me, session.user);
      toast.success("Account created. Welcome to VidyaAI.");
      const params = new URLSearchParams(window.location.search);
      const videoUrl = params.get("videoUrl");
      const next = resolveRedirectAfterLogin(params);
      router.replace(
        videoUrl
          ? "/dashboard/videos/upload?videoUrl=" + encodeURIComponent(videoUrl)
          : next,
      );
    },
    onError: (error) => {
      toast.error(getErrorMessage(error));
    },
  });
}

export function useLogout() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const clear = useAuthStore((s) => s.clear);

  return useMutation({
    mutationFn: () => authApi.logout(),
    onSettled: () => {
      clear();
      queryClient.clear();
      router.replace("/");
      toast.success("Signed out");
    },
  });
}

export function useUpdateProfile() {
  const queryClient = useQueryClient();
  const setUser = useAuthStore((s) => s.setUser);

  return useMutation({
    mutationFn: (payload: UpdateProfilePayload) =>
      authApi.updateProfile(payload),
    onSuccess: (user) => {
      setUser(user);
      queryClient.setQueryData(queryKeys.auth.me, user);
      toast.success("Profile updated");
    },
    onError: (error) => {
      if (error instanceof ApiError && error.validationErrors) {
        const first = Object.values(error.validationErrors)[0];
        toast.error(first || error.message);
        return;
      }
      toast.error(getErrorMessage(error));
    },
  });
}

export function useChangePassword() {
  return useMutation({
    mutationFn: (payload: ChangePasswordPayload) =>
      authApi.changePassword(payload),
    onSuccess: () => {
      toast.success("Password changed. Please sign in again on other devices.");
    },
    onError: (error) => {
      toast.error(getErrorMessage(error));
    },
  });
}
