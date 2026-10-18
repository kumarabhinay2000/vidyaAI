import { AuthGuard } from "@/components/auth/auth-guard";
import { PageHeader } from "@/components/common/page-header";
import { AdminTabs } from "@/components/admin/admin-shell";

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthGuard requireAdmin>
      <PageHeader
        title="Admin"
        description="Platform management and moderation."
      />
      <div className="mt-5">
        <AdminTabs />
      </div>
      <div className="mt-6">{children}</div>
    </AuthGuard>
  );
}
