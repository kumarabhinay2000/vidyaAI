"use client";

import { useState } from "react";
import {
    QueryClient,
    QueryClientProvider,
    type DefaultOptions,
} from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

const defaultOptions: DefaultOptions = {
    queries: {
        staleTime: 30_000,
        gcTime: 5 * 60_000,
        retry: 1,
        refetchOnWindowFocus: false,
        refetchOnReconnect: true,
    },
    mutations: {
        retry: 0,
    },
};

interface QueryProviderProps {
    children: React.ReactNode;
}

export function QueryProvider({ children }: QueryProviderProps) {
    const [client] = useState(
        () =>
            new QueryClient({
                defaultOptions,
            }),
    );

    return (
        <QueryClientProvider client={client}>
            {children}
            {process.env.NODE_ENV === "development" ? (
                <ReactQueryDevtools initialIsOpen={false} position="bottom" />
            ) : null}
        </QueryClientProvider>
    );
}