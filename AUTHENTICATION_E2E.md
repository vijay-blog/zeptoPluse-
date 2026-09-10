# NexaMart Email/Password E2E Checklist

1. Deploy the backend with Flyway migration V4.
2. Set `NEXAMART_JWT_SECRET` to a random 32+ character secret.
3. Set `NEXAMART_ADMIN_EMAIL`, `NEXAMART_ADMIN_PASSWORD`, and optional `NEXAMART_ADMIN_NAME` for the admin account.
4. Confirm the Railway service has a public HTTPS domain. Do not use `nexamart.railway.internal` from a phone.
5. Build Android with `-PnexamartApiUrl=https://PUBLIC-DOMAIN/api/v1/`.
6. Open Create Account and register a delivery-partner account.
7. The backend returns tokens and Android persists them securely; the app routes to Delivery Dashboard.
8. Sign out and sign back in with the same email/password.
9. Verify an admin account provisioned through Railway environment variables routes to Admin Dashboard.
10. Verify duplicate email registration returns a conflict message.
