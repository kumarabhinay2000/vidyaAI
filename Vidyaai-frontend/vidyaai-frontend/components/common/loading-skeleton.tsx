import { cn } from "@/lib/utils";

interface LoadingSkeletonProps {
    className?: string;
}

export function LoadingSkeleton({ className }: LoadingSkeletonProps) {
    return (
        <div
            className={cn(
                "animate-pulse rounded-md bg-surface-2",
                className,
            )}
            aria-hidden
        />
    );
}

export function PageLoadingSkeleton() {
    return (
        <div className="space-y-6">
            <LoadingSkeleton className="h-8 w-64" />
            <LoadingSkeleton className="h-4 w-96" />
            <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
                <LoadingSkeleton className="h-24" />
                <LoadingSkeleton className="h-24" />
                <LoadingSkeleton className="h-24" />
            </div>
            <LoadingSkeleton className="h-64 w-full" />
        </div>
    );
}