// =========================================================
// Generic API envelope + error types matching backend DTOs
// =========================================================

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
    timestamp: string;
}

export interface ErrorResponse {
    timestamp: string;
    status: number;
    error: string;
    errorCode: string;
    message: string;
    validationErrors?: Record<string, string> | null;
    path?: string;
    correlationId?: string;
}

export interface PageResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}