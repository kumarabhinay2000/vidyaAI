import { ForgotPasswordForm } from "@/components/auth/forgot-password-form";
import { AuthFrame } from "@/components/auth/auth-frame";
export default function Page() {
  return (
    <AuthFrame
      title="Let’s get you back in."
      description="Account recovery"
      footer={null}
    >
      <ForgotPasswordForm />
    </AuthFrame>
  );
}
