# NexaMart Admin + Delivery — API Requirements and Gap Analysis (Phase 0)

## Audit outcome

No backend/API implementation files are present in this repository, so endpoint discovery could not be performed from source here.

This document defines required backend contracts for this app. APIs below must be provided by the existing NexaMart backend (preferred) to avoid duplicate backend logic.

## Authentication and session

| API | Method | Authorization | Role | Purpose |
|---|---|---|---|---|
| `/api/v1/auth/login` | POST | Public | ADMIN, DELIVERY_PARTNER | Login with phone/email + password/OTP flow |
| `/api/v1/auth/send-otp` | POST | Public | ADMIN, DELIVERY_PARTNER | Send OTP when OTP-based auth is enabled |
| `/api/v1/auth/verify-otp` | POST | Public | ADMIN, DELIVERY_PARTNER | Verify OTP and issue tokens |
| `/api/v1/auth/refresh` | POST | Refresh token | ADMIN, DELIVERY_PARTNER | Rotate/refresh access token |
| `/api/v1/auth/logout` | POST | Bearer token | ADMIN, DELIVERY_PARTNER | Invalidate session/tokens |
| `/api/v1/auth/me` | GET | Bearer token | ADMIN, DELIVERY_PARTNER | Resolve authenticated user/role |

### Required login response

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "user": {
    "id": 123,
    "name": "string",
    "phone": "string",
    "role": "ADMIN"
  }
}
```

Allowed roles for this app:

- `ADMIN`
- `DELIVERY_PARTNER`

Any other role (including `CUSTOMER`) must be rejected by backend and blocked by app.

## Authentication Contract (Phase 2)

### Confirmed from current project documentation

- Base API namespace is expected under `/api/v1/*`.
- Auth path family is expected under `/api/v1/auth/*`.
- App supports only `ADMIN` and `DELIVERY_PARTNER`.
- Conceptual login response includes `accessToken`, `refreshToken`, and `user.role`.

### Not yet confirmed (must be provided by backend team)

- Exact login request fields (phone vs email vs identifier key names)
- Password vs OTP-only vs hybrid authentication policy
- Refresh request payload field names and rotation policy
- Logout endpoint auth requirements and expected response shape
- Token expiry durations and refresh expiry behavior
- Standard auth error response payload schema
- OTP endpoint payloads (`send-otp`, `verify-otp`, `resend-otp`) if OTP is enabled

### Current app integration behavior for unconfirmed fields

- Authentication request body mapping is intentionally isolated behind `AuthRequestContract`.
- Default implementation is `PendingBackendAuthRequestContract` and blocks live auth calls until contract keys are configured.
- This avoids hard-coding guessed payload keys and allows backend contract wiring in one place when confirmed.

## Admin APIs

| API | Method | Authorization | Role | Purpose |
|---|---|---|---|---|
| `/api/v1/admin/dashboard` | GET | Bearer token | ADMIN | KPI summary counts/sales/assignments |
| `/api/v1/admin/orders` | GET | Bearer token | ADMIN | Orders list + filtering + pagination |
| `/api/v1/admin/orders/{id}` | GET | Bearer token | ADMIN | Detailed order view |
| `/api/v1/admin/orders/{id}/status` | PATCH | Bearer token | ADMIN | Update order status with transition validation |
| `/api/v1/admin/orders/{id}/assign-delivery` | POST | Bearer token | ADMIN | Assign delivery partner |
| `/api/v1/admin/orders/{id}/reassign-delivery` | POST | Bearer token | ADMIN | Reassign delivery partner |
| `/api/v1/admin/orders/{id}/cancel` | POST/PATCH | Bearer token | ADMIN | Cancel order where permitted |
| `/api/v1/admin/products` | GET/POST | Bearer token | ADMIN | List/create products |
| `/api/v1/admin/products/{id}` | GET/PATCH/DELETE | Bearer token | ADMIN | Product details/update/delete-deactivate |
| `/api/v1/admin/categories` | GET/POST | Bearer token | ADMIN | List/create categories |
| `/api/v1/admin/categories/{id}` | GET/PATCH/DELETE | Bearer token | ADMIN | Category details/update/delete |
| `/api/v1/admin/customers` | GET | Bearer token | ADMIN | Customer list/search/filter |
| `/api/v1/admin/customers/{id}` | GET/PATCH | Bearer token | ADMIN | Customer details/status updates if supported |
| `/api/v1/admin/delivery-partners` | GET | Bearer token | ADMIN | Partner list/search/filter |
| `/api/v1/admin/delivery-partners/{id}` | GET/PATCH | Bearer token | ADMIN | Partner details/status/verification management |
| `/api/v1/admin/reports/*` | GET | Bearer token | ADMIN | Reporting endpoints (sales/order ops) |

## Phase 4 — Admin dashboard contract status

### Confirmed in this repository

- No backend source for dashboard APIs is available in this repository.
- No confirmed JSON contract for dashboard KPIs or recent orders is available.

### BACKEND REQUIRED — Admin dashboard endpoint

| Item | Requirement |
|---|---|
| Endpoint purpose | Return admin operational overview for KPI cards and recent orders in a single response |
| HTTP method | `GET` |
| Path | **BACKEND REQUIRED** (Android keeps endpoint contract-gated and does not guess a path) |
| Authorization | JWT bearer token |
| Required role | `ADMIN` only |
| Query parameters | Optional `recentOrdersLimit` integer (backend default if omitted) |

### Response fields required by Android dashboard

```json
{
  "totalOrders": 0,
  "todayOrders": 0,
  "pendingOrders": 0,
  "outForDelivery": 0,
  "deliveredToday": 0,
  "todaySales": 0.0,
  "currencyCode": "INR",
  "recentOrders": [
    {
      "orderId": "NM10025",
      "customerName": "Rahul",
      "amount": 850.0,
      "status": "OUT_FOR_DELIVERY",
      "createdAt": "2026-09-07T13:00:00Z"
    }
  ]
}
```

### Error response requirements

- `401`: session invalid/expired; Android transitions to login through centralized auth handling.
- `403`: non-admin role denied.
- `5xx` and network failures: retryable dashboard error without forced logout.
- Response body must not include stack traces, SQL internals, or secrets.

## Phase 5 — Admin order management contract status

### Confirmed in this repository

- Backend source code is not present in this repository, so live order endpoint contracts cannot be verified here.
- Android implementation is contract-aware and keeps order APIs disabled until contract keys/paths are confirmed.

### BACKEND REQUIRED — Admin orders APIs

| Capability | Requirement |
|---|---|
| Order list | `GET` admin orders endpoint with pagination, sorting, and server-side search/filter |
| Order details | `GET` admin order details endpoint by order id |
| Status update | Admin-authorized mutation endpoint with backend-side transition validation |
| Order cancellation | Admin-authorized cancellation endpoint with backend-side rule validation |
| Delivery assignment (foundation for later phase) | Assign/reassign endpoint contract confirmation required |

### Required order list query contract

- Page index parameter key
- Page size parameter key
- Sort parameter format and accepted values
- Search parameter key and supported search fields (order id/customer name/phone support matrix)
- Filter parameter keys and allowed enum values for:
  - order status
  - payment status
  - date range (if supported)
  - delivery filters (if supported)

### Required order response fields for Android

Order list summary:

- `orderId`
- `customerName`
- `customerPhone` (if permitted)
- `itemCount`
- `totalAmount`
- `currencyCode`
- `status`
- `paymentStatus`
- `deliveryStatus` (if available)
- `createdAt`

Order details:

- `orderId`, `createdAt`, current `status`
- customer block (`name`, optional `phone`, optional `address`)
- items block (`productName`, `quantity`, `unitPrice`, `lineTotal`)
- payment block (`method`, `status`, optional `transactionReference`)
- totals block (`subtotal`, `deliveryFee`, `discount`, `tax`, `grandTotal`, `currencyCode`)
- delivery block (`status`, `partnerName`, `assignedAt`) when available
- timeline/status history when available
- `allowedTransitions` and `canCancel` flags where mutation UI is enabled

### Status transition and conflict requirements

- Backend remains final authority for valid status transitions.
- Backend should return transition hints (`allowedTransitions`) when possible.
- Concurrent mutation conflicts should return `409` with safe error payload.
- Android maps conflicts to a refresh-first message and does not overwrite stale state.

### Error response requirements for order management

- `401`: token/session invalid -> centralized auth recovery.
- `403`: role denied -> explicit permission message.
- `404`: order not found.
- `409`: order updated elsewhere; refresh required.
- `422`: validation/transition rejection.
- `5xx`: retryable server errors.
- No stack traces, SQL internals, or secret/internal fields in error payloads.

## Phase 6 — Admin delivery partner management contract status

### Confirmed in this repository

- No Java Spring Boot source, entity definitions, role enums, migrations, or controllers are present.
- Existing backend partner states and endpoint contracts therefore cannot be verified from this workspace.
- Android uses `PendingBackendDeliveryPartnerContract`; live calls remain blocked until paths, query keys, action payloads, and enum values are confirmed.

### BACKEND REQUIRED — Admin delivery partner APIs

| Capability | Requirement |
|---|---|
| Partner list | ADMIN-authorized `GET` endpoint with server-side pagination and search/filter support |
| Partner details | ADMIN-authorized `GET` endpoint by partner ID |
| Partner action | ADMIN-authorized mutation endpoint for only backend-supported transitions |
| Current orders | Include current assigned order summaries in details, or provide a confirmed related endpoint |
| Recent history/statistics | Include only when authorized and supported by backend |

The exact paths and HTTP mutation method are **BACKEND REQUIRED**. Android does not assume that the conceptual `/api/v1/admin/delivery-partners` paths listed above exist.

### Required list query contract

- Page index and page-size parameter keys.
- Search parameter key and supported fields (name, phone, email, or partner ID).
- Account-status, verification-status, and availability filter keys and exact accepted enum values.
- Spring Data-compatible page response fields, or a confirmed mapping for:
  - content
  - page number
  - page size
  - total pages
  - total elements
  - last-page indicator

### Required response fields

Partner summary:

- `partnerId`, `name`
- optional `phone`, `profileImageUrl`
- `accountStatus`, `verificationStatus`, `availability`, `workState`
- optional `activeDeliveries`
- nullable backend-authoritative `isAssignable`

Partner details:

- summary identity and state fields
- optional `email`, `registeredAt`, `lastActiveAt`
- optional vehicle type, vehicle number, and safe license reference
- optional delivery statistics
- current-order and recent-history summaries (`orderId`, status, timestamp)
- nullable `isAssignable`
- `allowedActions` containing only currently valid Admin transitions

### Status and action contract

- Backend enum names are authoritative and must be confirmed before enabling the contract.
- Backend must validate every verification/account transition transactionally.
- Android does not infer valid actions from account or verification status; it renders only `allowedActions`.
- Rejection or suspension reasons are sent only when the confirmed backend contract supports/requires them.
- Admin cannot modify online/offline availability unless a future backend contract explicitly authorizes it.

### Authorization, privacy, and conflict requirements

- All Admin partner-management endpoints must enforce the backend project's ADMIN authorization convention.
- DELIVERY_PARTNER and CUSTOMER roles must receive `403`.
- DTOs must exclude passwords, tokens, internal security fields, and unnecessary identity documents.
- `401`: invalid session; Android invokes centralized session-expiration handling.
- `404`: partner not found.
- `409`: stale state or active-delivery conflict; Android reports the conflict and refreshes details.
- `422`: invalid transition or validation failure.
- `429` and `5xx`: safe mapped errors without backend internals.

## Phase 7 — Admin product management contract status

### Confirmed in this repository

- No Java Spring Boot source, entity definitions, migrations, or controllers exist in this repository, so product/category endpoint contracts cannot be verified here.
- Android uses `PendingBackendProductManagementContract`; live calls remain blocked until paths, query keys, create/update/action body keys, and enum values are confirmed.

### BACKEND REQUIRED — Admin product APIs

| Capability | Requirement |
|---|---|
| Product list | ADMIN-authorized `GET` endpoint with server-side pagination, search, status/category filters, and sort |
| Product details | ADMIN-authorized `GET` endpoint by product id |
| Category lookup | ADMIN-authorized `GET` endpoint returning `categoryId`/`name` pairs only, for product-form selection (full category CRUD is explicitly out of scope for this phase) |
| Product creation | ADMIN-authorized mutation endpoint with backend-side field validation |
| Product update | ADMIN-authorized mutation endpoint with backend-side field validation |
| Product action | ADMIN-authorized mutation endpoint for only backend-supported transitions (`ACTIVATE`/`DEACTIVATE`/`DELETE`) |

The exact paths and HTTP mutation methods are **BACKEND REQUIRED**. Android does not assume any conceptual `/api/v1/admin/products` path exists.

### Required list query contract

- Page index and page-size parameter keys.
- Search parameter key and supported fields (name, SKU, or category).
- Status and category filter keys and exact accepted enum/id values.
- Sort parameter key and accepted values (e.g. newest, name, price ascending/descending).
- Spring Data-compatible page response fields, or a confirmed mapping for content/page number/page size/total pages/total elements/last-page indicator.

### Required response fields

Product summary:

- `productId`, `name`
- optional `categoryId`, `categoryName`
- exact decimal `price` and optional backend-computed `discountedPrice` (never computed client-side)
- `currencyCode`, optional `stock`, optional `unit`
- `status`, `availability`
- optional `imageUrl` (image storage/CDN contract, and an approved image-loading dependency, are both unconfirmed; the app shows a static placeholder instead of loading this URL)

Product details:

- summary identity/state fields
- optional `description`, `discountPercent`, `sku`, `createdAt`, `updatedAt`
- `allowedActions` containing only currently valid Admin transitions (`ACTIVATE`/`DEACTIVATE`/`DELETE`/`EDIT`)

Category option (lookup only):

- `categoryId`, `name`

### Create/update request contract

- Exact required vs optional field set for create and update is **BACKEND REQUIRED**.
- Android models `name`, `description`, `categoryId`, `price`, `discountPercent`, `stock`, `sku`, and `unit` as a draft, but only performs local baseline validation (name required, category required, price a non-negative decimal, discount 0-100 when provided, stock a non-negative integer when provided). The backend remains authoritative for all business validation.
- Prices/discounts must be sent and returned as exact decimal strings; Android never derives a final/discounted price itself.

### Status and action contract

- Backend enum names are authoritative and must be confirmed before enabling the contract.
- Android does not infer valid actions from product status/availability locally; it renders only `allowedActions`.
- Deleting a product must be backend-validated (e.g., no undeletable references) before returning success.

### Authorization, privacy, and conflict requirements

- All Admin product-management endpoints must enforce the backend project's ADMIN authorization convention.
- DELIVERY_PARTNER and CUSTOMER roles must receive `403`.
- Product data returned to Admin must remain compatible with what customer-facing and historical order flows already expect (e.g., product name/price snapshots on existing orders must not be silently changed by catalog edits).
- `401`: invalid session; Android invokes centralized session-expiration handling.
- `404`: product not found.
- `409`: stale state/conflicting edit; Android refreshes read-only details, while the edit form preserves the unsaved draft and asks the Admin to review before retrying.
- `422`: validation failure.
- `429` and `5xx`: safe mapped errors without backend internals.

## Delivery partner APIs

| API | Method | Authorization | Role | Purpose |
|---|---|---|---|---|
| `/api/v1/delivery/dashboard` | GET | Bearer token | DELIVERY_PARTNER | Delivery dashboard counters and summary |
| `/api/v1/delivery/orders` | GET | Bearer token | DELIVERY_PARTNER | Assigned/active orders |
| `/api/v1/delivery/orders/{id}` | GET | Bearer token | DELIVERY_PARTNER | Delivery order details |
| `/api/v1/delivery/orders/{id}/accept` | POST | Bearer token | DELIVERY_PARTNER | Accept assigned order |
| `/api/v1/delivery/orders/{id}/picked-up` | POST/PATCH | Bearer token | DELIVERY_PARTNER | Mark picked up |
| `/api/v1/delivery/orders/{id}/out-for-delivery` | POST/PATCH | Bearer token | DELIVERY_PARTNER | Start final leg |
| `/api/v1/delivery/orders/{id}/delivered` | POST/PATCH | Bearer token | DELIVERY_PARTNER | Mark delivered (OTP/proof if needed) |
| `/api/v1/delivery/history` | GET | Bearer token | DELIVERY_PARTNER | Delivery history with date filters |
| `/api/v1/delivery/earnings` | GET | Bearer token | DELIVERY_PARTNER | Earnings summary and breakdown |
| `/api/v1/delivery/profile` | GET/PATCH | Bearer token | DELIVERY_PARTNER | Profile and availability updates |
| `/api/v1/delivery/availability` | PATCH | Bearer token | DELIVERY_PARTNER | ONLINE/OFFLINE state |

## Notifications APIs (if FCM used)

| API | Method | Authorization | Role | Purpose |
|---|---|---|---|---|
| `/api/v1/notifications/register-device` | POST | Bearer token | ADMIN, DELIVERY_PARTNER | Register device push token |
| `/api/v1/notifications` | GET | Bearer token | ADMIN, DELIVERY_PARTNER | Notification list/history |
| `/api/v1/notifications/{id}/read` | PATCH | Bearer token | ADMIN, DELIVERY_PARTNER | Mark notification read |

## Required request/response model families

- Auth: login, OTP send/verify, refresh, logout
- User/session: profile + role + permission set
- Dashboard metrics: counts/sales/delivery ops
- Orders: list/detail/timeline/status mutation
- Delivery assignments and transitions
- Product/category CRUD payloads
- Delivery partner CRUD/status payloads
- Customer list/detail payloads
- Notification payloads and click-routing metadata
- Optional delivery completion payloads: OTP/proof photo/notes

## Missing data from this repository (backend gap)

The following could not be confirmed from repository code:

- Actual backend base URL and API version
- Concrete endpoint paths/methods/payload schemas
- Existing role enum names and permission matrix
- Existing order/delivery status constants
- Refresh-token strategy and token expiry windows
- Notification infrastructure (FCM/SSE/WebSocket)
- Payment status/method enums and source-of-truth fields

Backend/API contract confirmation is required before implementing full production integrations in Phases 2+.


## Phase 15 — Delivery History
- Required backend contract: delivery-partner history list path.
- Query contract: page, pageSize, optional search, status, fromDate, toDate.
- Response can reuse the delivery order page summary shape.
- History must be scoped server-side to the authenticated delivery partner.
- Suggested business statuses are UI filters only; backend remains authoritative and may map different status values.
- Android currently returns an explicit unavailable state until the exact Spring Boot contract is confirmed.

## Delivery Earnings API (Phase 16)
The Android module now expects the backend to explicitly define:
- earnings summary endpoint and query semantics for optional from/to dates;
- paginated earnings history endpoint and query semantics for page, pageSize, fromDate, toDate;
- summary fields: currencyCode, today, thisWeek, thisMonth, completedDeliveries, pendingPayout, totalEarned;
- history fields: id, orderId, earnedAt, amount, currencyCode, status, description;
- ADMIN must not be able to use delivery-partner endpoints unless separately authorized; DELIVERY_PARTNER access is required for these screens.
No endpoint path or payload mapping is invented in the pending contract.

## Phase 17 — Delivery Notifications

The Android delivery notification center is contract-gated until the Java Spring Boot backend contract is confirmed. Required backend capabilities:
- GET notification page for the authenticated DELIVERY_PARTNER with page/pageSize and optional unreadOnly query.
- POST/PUT operation to mark one notification read.
- POST/PUT operation to mark all notifications read.
- Response fields: id, title, message, createdAt, read, type, optional orderId/actionUrl, pagination metadata, and optional unreadCount.
- Server-side authorization must restrict notification access to the authenticated partner.
- If notification references an order, the backend must ensure the referenced order is accessible to that partner.
- Do not expose secrets, tokens, internal metadata, or unrelated customer PII in notification payloads.

## Phase 18 — Delivery Partner Profile
- GET delivery-partner profile endpoint is pending confirmation from the Java Spring Boot backend.
- PUT/PATCH profile update endpoint and exact editable-field/body contract are pending confirmation.
- The Android feature is contract-gated and does not guess endpoint paths, request fields, or mutability rules.
- Backend should return verification/account status and an explicit editable-field list if field-level editing is supported.
- Profile image upload is intentionally not implemented until the real media/upload contract is confirmed.

## Phase 19 — Delivery Availability

Android is contract-ready for a delivery-partner availability read/update API. The backend must confirm the exact GET/read path, update path, request field/body, response fields, authorization, and business rules (including whether a partner with active orders may go offline). No endpoint or payload is assumed by the Android client. The UI uses backend `available`, `status`, `canChange`, `reason`, and `updatedAt` when supplied.


## Phase 20 — Admin Settings API Requirements
If server-side settings are required, the Spring Boot backend should expose an authenticated ADMIN-only configuration contract. Android must consume only confirmed fields and permissions; it must not invent endpoints or mutate business/security/payment configuration locally.
Required contract areas when supported: settings summary, editable-field metadata, update endpoint, validation/error payloads, audit information, and optimistic-concurrency/version handling where applicable.

## Phase 22 — Offline / Network Resilience
- Android now observes device internet capability through `NetworkConnectivityMonitor`.
- A global offline banner is shown while the device has no usable internet capability.
- `OfflineAwareInterceptor` fails requests fast while known offline; it does not queue or retry mutations.
- Phase 21 read retry behavior remains responsible for safe GET/HEAD retries when connectivity exists.
- The app does not fabricate cached server state. Existing feature unavailable/error states remain authoritative until a real backend response is available.
- No background synchronization, offline mutation queue, or conflict-merging strategy is enabled because the Spring Boot backend contract does not define one yet.

## Authentication — implemented against NexaMart Spring Boot backend

The authentication contract is now confirmed and wired:

- `POST /api/v1/auth/register` body `{name,email,password}`; response contains `accessToken`, `refreshToken`, `expiresInSeconds`, and `user`.
- `POST /api/v1/auth/login` body `{email,password}`; same response shape.
- `POST /api/v1/auth/refresh` body `{refreshToken}`.
- `POST /api/v1/auth/logout` body `{refreshToken}`.
- Public registration creates `DELIVERY_PARTNER`; ADMIN accounts are provisioned by backend environment variables.
- Android must use the public HTTPS Railway URL; `nexamart.railway.internal` is private and is not reachable from normal devices.
