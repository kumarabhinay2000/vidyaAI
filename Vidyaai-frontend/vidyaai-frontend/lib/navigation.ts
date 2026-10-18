import {
  BookOpen,
  BarChart3,
  Bot,
  LayoutDashboard,
  Trophy,
  User,
  Video,
  type LucideIcon,
} from "lucide-react";

export interface NavItem {
  label: string;
  href: string;
  icon: LucideIcon;
  description?: string;
  adminOnly?: boolean;
}

export interface NavSection {
  title?: string;
  items: NavItem[];
}

export const primaryNavigation: NavSection = {
  items: [
    {
      label: "Overview",
      href: "/dashboard",
      icon: LayoutDashboard,
      description: "Overview of your learning",
    },
    {
      label: "My lectures",
      href: "/dashboard/videos",
      icon: Video,
      description: "Uploaded and processed videos",
    },
    {
      label: "Progress",
      href: "/dashboard/progress",
      icon: BarChart3,
      description: "Your learning metrics",
    },
    {
      label: "Leaderboard",
      href: "/dashboard/leaderboard",
      icon: Trophy,
      description: "Top performers this week",
    },
    {
      label: "AI Tutor",
      href: "/dashboard/chat",
      icon: Bot,
      description: "Ask questions across your videos",
    },
  ],
};

export const accountNavigation: NavSection = {
  title: "Account",
  items: [
    {
      label: "Profile",
      href: "/dashboard/profile",
      icon: User,
      description: "Manage your account",
    },
  ],
};
export const adminNavigation: NavSection = {
  items: [
    { label: "Overview", href: "/admin", icon: LayoutDashboard },
    { label: "Users", href: "/admin/users", icon: User },
    { label: "Videos", href: "/admin/videos", icon: Video },
    { label: "Return to learning", href: "/dashboard", icon: BookOpen },
  ],
};
