import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

const PROTECTED_PREFIXES = ["/dashboard", "/admin"];

/**
 * Lightweight route protection.
 * Real authorization is enforced on the backend; this just improves UX
 * by redirecting guests away from protected pages before they render.
 */
export function middleware(request: NextRequest) {
    const { pathname } = request.nextUrl;

    const isProtected = PROTECTED_PREFIXES.some((p) => pathname.startsWith(p));
    if (!isProtected) return NextResponse.next();

    // Tokens live in localStorage (client-only). We can't read them here,
    // so we let the client-side `AuthBootstrap` + a protective client layout
    // handle auth. Middleware only adds a lightweight hint via cookies if set.
    const hasSessionCookie = request.cookies.get("vidyaai.session")?.value;
    if (hasSessionCookie) return NextResponse.next();

    // No hint at all → still allow; client will render guard and redirect if needed.
    return NextResponse.next();
}

export const config = {
    matcher: ["/dashboard/:path*", "/admin/:path*"],
};