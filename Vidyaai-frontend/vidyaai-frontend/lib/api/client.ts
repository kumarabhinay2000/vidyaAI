import axios, {
  type AxiosError,
  type AxiosInstance,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig,
} from "axios";

import { useAuthStore } from "@/stores/auth-store";
import { ApiError } from "./errors";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";
const REQUEST_TIMEOUT_MS = 30_000;

// Prevent concurrent refresh storms.
let refreshPromise: Promise<string | null> | null = null;

interface RetryConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

function createClient(): AxiosInstance {
  const instance = axios.create({
    baseURL: BASE_URL,
    timeout: REQUEST_TIMEOUT_MS,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    withCredentials: false,
  });

  // -------- Request interceptor: attach Bearer token --------
  instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = useAuthStore.getState().accessToken;
      if (token) {
        config.headers.set("Authorization", `Bearer ${token}`);
      }
      return config;
    },
    (error) => Promise.reject(error),
  );

  // -------- Response interceptor: 401 → refresh once → retry --------
  instance.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const original = error.config as RetryConfig | undefined;
      const status = error.response?.status;

      // Only attempt refresh for 401 on non-refresh endpoints.
      const isRefreshCall =
        original?.url?.includes("/auth/refresh-token") ?? false;

      if (status === 401 && original && !original._retry && !isRefreshCall) {
        original._retry = true;

        // Ensure only one refresh call runs at a time.
        if (!refreshPromise) {
          refreshPromise = performRefresh().finally(() => {
            refreshPromise = null;
          });
        }

        const newToken = await refreshPromise;

        if (newToken) {
          original.headers = original.headers ?? {};
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          (original.headers as any).Authorization = `Bearer ${newToken}`;
          return instance(original);
        }

        // Refresh failed → clear session and let the UI react.
        useAuthStore.getState().clear();
      }

      throw ApiError.fromAxiosError(error);
    },
  );

  return instance;
}

/**
 * Calls the backend refresh-token endpoint using the refresh token
 * currently stored in the auth store. Returns the new access token on
 * success, or null if the refresh fails.
 */
async function performRefresh(): Promise<string | null> {
  const { refreshToken, setSession, clear } = useAuthStore.getState();
  if (!refreshToken) return null;

  try {
    const response = await axios.post(
      `${BASE_URL}/auth/refresh-token`,
      { refreshToken },
      {
        headers: { "Content-Type": "application/json" },
        timeout: REQUEST_TIMEOUT_MS,
      },
    );

    const session = response.data?.data;
    if (!session?.accessToken || !session?.refreshToken || !session?.user) {
      clear();
      return null;
    }

    setSession(session);
    return session.accessToken as string;
  } catch {
    clear();
    return null;
  }
}

export const apiClient = createClient();

/**
 * Small wrapper to keep call-sites readable and typed.
 */
export async function apiRequest<T>(config: AxiosRequestConfig): Promise<T> {
  const response = await apiClient.request<{ data: T }>(config);
  return response.data.data;
}
