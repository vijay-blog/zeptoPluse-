# NexaMart Authentication Setup

Production authentication is email/password for Admin and Delivery Partner accounts.

## Railway variables
- `NEXAMART_JWT_SECRET`: random secret, 32+ characters.
- `NEXAMART_ADMIN_EMAIL`: bootstrap admin email.
- `NEXAMART_ADMIN_PASSWORD`: bootstrap admin password, 8+ characters.
- `NEXAMART_ADMIN_NAME`: optional display name.

The Admin account is provisioned server-side and is never hard-coded in the Android application.
Delivery Partner registration uses `POST /api/v1/auth/register` and creates a DELIVERY_PARTNER account.
Login uses `POST /api/v1/auth/login`; refresh uses `/api/v1/auth/refresh`; logout uses `/api/v1/auth/logout`.

For physical devices use the public HTTPS Railway domain, not `*.railway.internal`.
