# VidyaAI — Frontend

Next.js 16 + TypeScript + Tailwind 4 + shadcn/ui frontend for the VidyaAI
platform. Talks to the VidyaAI API Gateway, which fans out to the User, Video,
AI, and Quiz microservices.

## Stack

- Next.js 16 (App Router, Turbopack)
- TypeScript 5
- Tailwind CSS 4
- shadcn/ui (base-nova)
- TanStack Query v5
- Zustand
- React Hook Form + Zod
- Recharts
- Sonner (toasts)
- lucide-react

## Local development

```bash
npm install
cp .env.example .env.local
npm run dev
```

Frontend: http://localhost:3000
API Gateway: http://localhost:8080

## Environment

| Variable | Purpose |
|---|---|
| `NEXT_PUBLIC_API_URL` | Base URL of the API Gateway (e.g. `http://localhost:8080/api`) |
| `NEXT_PUBLIC_WS_URL` | WebSocket endpoint for live updates |
| `NEXT_PUBLIC_APP_URL` | Public URL of the frontend (used in metadata) |

## Scripts

```bash
npm run dev       # Local dev server
npm run build     # Production build
npm run start     # Production server
npm run lint      # ESLint
```

## Architecture

- `app/(public)` — marketing and auth screens
- `app/(dashboard)` — authenticated app (dashboard, videos, progress, admin)
- `components/providers` — root providers (Query, Theme, Auth bootstrap, WS stream)
- `lib/api` — axios client with automatic refresh-token flow
- `lib/websocket` — auto-reconnecting WebSocket client
- `hooks/` — TanStack Query hooks per resource
- `stores/` — Zustand stores (auth, ui)

## Authentication

JWT access token in Zustand (memory + localStorage), refresh token in the
same store. On any 401, the axios interceptor calls `POST /api/auth/refresh-token`
exactly once, updates the session, and retries the original request.

## Realtime

A single WebSocket connection is opened at the app root. Message types used:

- `video.status` — processing progress per video
- `notification` — user-facing notifications

The connection auto-reconnects with exponential backoff. When disconnected,
the processing status view falls back to 5s polling.

## Deployment

### Vercel

1. Import the repo into Vercel.
2. Set the environment variables from `.env.production.example`.
3. Deploy. No additional configuration needed.

### Docker

```bash
docker build -t vidyaai-frontend .
docker run --rm -p 3000:3000 \
  -e NEXT_PUBLIC_API_URL=http://localhost:8080/api \
  -e NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws \
  vidyaai-frontend
```

## License

Apache 2.0