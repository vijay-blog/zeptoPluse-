# NexaMart Partner Security

## Phase 23 hardening

- Session tokens are stored using AndroidX Security `EncryptedSharedPreferences` with an AES-256-GCM master key.
- Authentication/session preferences are excluded from Android Auto Backup and device transfer.
- Cleartext HTTP traffic is explicitly disabled.
- Network trust is restricted to system certificate authorities; no user-added CA trust is enabled by the app.
- Release builds disable network logging. Debug network logging is BASIC only and must never be expanded to headers/bodies containing credentials or customer data.
- The main activity uses `FLAG_SECURE` to reduce accidental screenshot/screen-recording exposure of orders, addresses, customer data and earnings.
- No credentials, access tokens, refresh tokens, OTPs or passwords are written to application logs.
- Only the launcher activity is exported; no unnecessary exported services/receivers/providers are declared.
- Delivery/admin authorization remains enforced by the authenticated role and navigation guard; UI visibility is not treated as authorization.
- Backend authorization remains authoritative for every protected API and state-changing action.
- External intents (maps, dialer, browser/share) must be treated as untrusted boundaries and should never receive bearer tokens or authentication secrets.

## Backend requirements

The Spring Boot backend must continue to enforce role/permission checks server-side, validate ownership of delivery/order/customer resources, expire/revoke sessions, rate-limit authentication, and avoid returning secrets or unnecessary PII.
