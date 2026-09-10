# NexaMart Railway deployment

Public API base URL used by the mobile apps:

`https://zeptopluse-production.up.railway.app/api/v1/`

## Required Railway variables

- `MYSQLHOST`, `MYSQLPORT`, `MYSQL_DATABASE`, `MYSQLUSER`, `MYSQL_ROOT_PASSWORD`
- `NEXAMART_JWT_SECRET` — use a random value with at least 32 characters
- `NEXAMART_ADMIN_EMAIL` — the email used for the Admin app
- `NEXAMART_ADMIN_PASSWORD` — the Admin password (8+ characters)
- `NEXAMART_ADMIN_NAME` — optional
- `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET` only if online payments are enabled

Do not hard-code the Admin password in Android or commit it to Git.

Flyway V5 adds the account/operations fields required by the Admin + Delivery app. After deployment, confirm `/api/v1/health` returns HTTP 200 before installing the mobile apps.
