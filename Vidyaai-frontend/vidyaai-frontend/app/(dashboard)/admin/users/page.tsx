"use client";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { apiRequest } from "@/lib/api/client";
import type { User } from "@/types/user";
import { QueryError } from "@/components/common/query-error";
export default function Page() {
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const query = useQuery({
    queryKey: ["admin", "users"],
    queryFn: () =>
      apiRequest<User[]>({ url: "/users/admin/all", method: "GET" }),
  });
  const filtered = (query.data ?? []).filter((u) =>
    (u.fullName + " " + u.email).toLowerCase().includes(search.toLowerCase()),
  );
  return (
    <div className="space-y-5">
      <div>
        <h2 className="text-xl font-medium">Registered users</h2>
        <p className="mt-2 text-sm text-text-secondary">
          Read-only account directory. Role and account changes are not
          available here.
        </p>
      </div>
      <label className="block max-w-md">
        <span className="sr-only">Search users</span>
        <input
          className="w-full rounded-md border border-border bg-surface px-4"
          placeholder="Search by name or email"
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(0);
          }}
        />
      </label>
      {query.isLoading ? (
        <p role="status">Loading users…</p>
      ) : query.isError ? (
        <QueryError retry={() => void query.refetch()} />
      ) : (
        <>
          <div className="overflow-x-auto rounded-lg border border-border bg-surface">
            <table className="w-full min-w-[640px] text-left text-sm">
              <thead>
                <tr>
                  {["Name", "Email", "Role", "Status"].map((h) => (
                    <th
                      key={h}
                      className="border-b border-border p-4 font-medium"
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.slice(page * 10, page * 10 + 10).map((u) => (
                  <tr key={u.id} className="border-b border-border">
                    <td className="p-4">{u.fullName}</td>
                    <td className="p-4">{u.email}</td>
                    <td className="p-4">{u.role}</td>
                    <td className="p-4">{u.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {filtered.length === 0 && (
              <p className="p-6 text-text-secondary">No matching users.</p>
            )}
          </div>
          <div className="flex items-center justify-between">
            <button
              className="quiet-link disabled:opacity-40"
              disabled={page === 0}
              onClick={() => setPage((p) => p - 1)}
            >
              ← Previous
            </button>
            <span className="text-sm text-text-secondary">
              Page {page + 1} of {Math.max(1, Math.ceil(filtered.length / 10))}
            </span>
            <button
              className="quiet-link disabled:opacity-40"
              disabled={(page + 1) * 10 >= filtered.length}
              onClick={() => setPage((p) => p + 1)}
            >
              Next →
            </button>
          </div>
        </>
      )}
    </div>
  );
}
