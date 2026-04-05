# Custom Firebase Email Action Flow

This project now uses backend-generated Firebase action links and custom HTML email templates for both:
- Email verification
- Password reset

## 1) Required configuration

Set these in [backend/src/main/resources/db.properties](../backend/src/main/resources/db.properties):

- app.authActionUrl
- mail.smtp.host
- mail.smtp.port
- mail.smtp.username
- mail.smtp.password
- mail.smtp.auth
- mail.smtp.starttls.enable
- mail.from.email
- mail.from.name

Recommended for this deployment:
- app.authActionUrl=https://34.126.65.182.nip.io/auth-action.html

## 2) Mail provider options

Use any SMTP provider. Common examples:

- Gmail SMTP
  - host: smtp.gmail.com
  - port: 587
  - starttls: true
  - auth: true
  - use an App Password

- SendGrid SMTP
  - host: smtp.sendgrid.net
  - port: 587
  - username: apikey
  - password: your SendGrid API key

- Mailgun SMTP
  - host: smtp.mailgun.org
  - port: 587

## 3) Template location

Custom branded template file:

- [backend/src/main/resources/email-templates/account-action-email.html](../backend/src/main/resources/email-templates/account-action-email.html)

It is parameterized server-side and reused for both Verify and Reset actions.

## 4) End-to-end verification flow

1. Frontend creates Firebase user (email/password).
2. Frontend registers user in backend (`/api/auth/register` or `/api/auth/register-teacher`).
3. Backend generates verification link using Firebase Admin SDK.
4. Backend sends custom HTML email with Verify Email button.
5. Button lands at `app.authActionUrl` with `mode`, `oobCode`, and related params.
6. [html/auth-action.html](../html/auth-action.html) + [scripts/auth-action.js](../scripts/auth-action.js) applies verification and redirects to login.

## 5) End-to-end password reset flow

1. User clicks Forgot Password on login page.
2. Frontend calls backend `/api/auth/forgot-password-init`.
3. Backend generates Firebase reset link via Admin SDK.
4. Backend sends custom HTML email with Reset Password button.
5. Button lands at `app.authActionUrl` with reset params.
6. [scripts/auth-action.js](../scripts/auth-action.js) verifies reset code, accepts new password, confirms reset with Firebase, then redirects to login.

## 6) Notes

- Firebase remains the source of truth for email verification and password reset validity.
- If SMTP config is missing, backend returns a clear error and includes reason in API response.
