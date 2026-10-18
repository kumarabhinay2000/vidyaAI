"use client";

import { PageHeader } from "@/components/common/page-header";
import { SectionCard } from "@/components/ai/section-card";
import { ChangePasswordForm } from "@/components/settings/change-password-form";
import { DangerZone } from "@/components/settings/danger-zone";
import { ProfileForm } from "@/components/settings/profile-form";
import { UserAvatar } from "@/components/common/user-avatar";
import { useAuthStore } from "@/stores/auth-store";

export default function ProfilePage() {
  const user = useAuthStore((s) => s.user);

  return (
    <>
      <PageHeader
        title="Profile"
        description="Manage your account, password, and sessions."
      />

      <div className="mt-6 space-y-6">
        <SectionCard title="Identity" description="How you appear in the app.">
          <div className="flex items-center gap-4">
            <UserAvatar
              firstName={user?.firstName}
              lastName={user?.lastName}
              imageUrl={user?.profilePicUrl}
              size="lg"
            />
            <div>
              <p className="text-sm font-medium text-text-primary">
                {user?.firstName} {user?.lastName ?? ""}
              </p>
              <p className="text-[11px] text-text-tertiary">{user?.email}</p>
              <p className="mt-1 inline-flex items-center rounded-full bg-surface-2 px-2 py-0.5 text-[10px] uppercase tracking-wide text-text-secondary">
                {user?.planType ?? "FREE"} · {user?.role ?? "STUDENT"}
              </p>
            </div>
          </div>
        </SectionCard>

        <SectionCard
          title="Profile details"
          description="Update your name and photo."
        >
          <ProfileForm />
        </SectionCard>

        <SectionCard
          title="Change password"
          description="Choose a strong, unique password."
        >
          <ChangePasswordForm />
        </SectionCard>

        <DangerZone />
      </div>
    </>
  );
}
