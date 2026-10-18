import { AxiosError, type AxiosResponse } from "axios";

import type { ErrorResponse } from "@/types/api";

export class ApiError extends Error {
  public readonly status: number;
  public readonly errorCode: string;
  public readonly validationErrors?: Record<string, string> | null;
  public readonly correlationId?: string | null;
  public readonly originalError?: unknown;

  constructor(params: {
    message: string;
    status: number;
    errorCode: string;
    validationErrors?: Record<string, string> | null;
    correlationId?: string | null;
    originalError?: unknown;
  }) {
    super(params.message);
    this.name = "ApiError";
    this.status = params.status;
    this.errorCode = params.errorCode;
    this.validationErrors = params.validationErrors ?? null;
    this.correlationId = params.correlationId ?? null;
    this.originalError = params.originalError;
  }

  static fromAxiosError(error: AxiosError<unknown>): ApiError {
    const status = error.response?.status ?? 0;
    const body = error.response?.data as Partial<ErrorResponse> | undefined;

    if (body && typeof body === "object" && "message" in body) {
      return new ApiError({
        message: body.message || "Request failed",
        status,
        errorCode: body.errorCode || "UNKNOWN",
        validationErrors: body.validationErrors ?? null,
        correlationId: body.correlationId ?? null,
        originalError: error,
      });
    }

    if (error.code === "ECONNABORTED") {
      return new ApiError({
        message: "Request timed out. Please try again.",
        status,
        errorCode: "TIMEOUT",
        originalError: error,
      });
    }

    if (!error.response) {
      return new ApiError({
        message: "Network error. Please check your connection.",
        status: 0,
        errorCode: "NETWORK_ERROR",
        originalError: error,
      });
    }

    return new ApiError({
      message: error.message || "Unexpected error",
      status,
      errorCode: "UNKNOWN",
      originalError: error,
    });
  }
}

export function isApiError(value: unknown): value is ApiError {
  return value instanceof ApiError;
}

export function unwrap<T>(response: AxiosResponse<{ data: T }>): T {
  return response.data.data;
}
