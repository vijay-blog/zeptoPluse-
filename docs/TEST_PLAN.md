# NexaMart Admin + Delivery — Phase 25 Test Plan

## Scope

Regression coverage for the native Kotlin Admin + Delivery application through Phase 24.

## Automated test areas

- Authentication/session lifecycle and logout
- Role isolation and protected navigation
- Admin dashboard/orders/delivery-partner/product/category/customer ViewModels and repositories
- Delivery dashboard/order workflow/history/earnings/notifications/profile/availability models
- API error parsing and HTTP failure mapping
- Safe read retry and offline connectivity behavior
- Currency/count formatting
- Navigation destination integrity

## Instrumentation test areas

- Login UI visibility and validation
- Auth-driven role routing
- Delivery partner management UI
- Product management UI
- Navigation smoke coverage where Android test infrastructure is available

## Contract-gated behavior

Live API success-path tests cannot be executed without the real Java Spring Boot backend contract and a reachable test environment. The Android code intentionally uses pending contract implementations rather than invented endpoints.

## Regression rule

No change in Phase 25 should alter Customer App code or introduce a second backend. Admin and Delivery authorization boundaries must remain mutually exclusive.
