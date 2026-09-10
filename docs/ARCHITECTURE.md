# NexaMart Admin + Delivery — Architecture

## Repository and platform baseline

This repository contains a native Android Admin + Delivery client, with:

- Gradle Kotlin DSL build
- Single `:app` module
- Android package: `com.daily.nexamartpartner`
- Phased implementation across foundation, authentication, protected navigation, and Admin Dashboard foundation

## Current technology identified

- Platform: Android (Kotlin)
- Build: Gradle (`build.gradle.kts`)
- UI baseline: Material Components (migrated in Phase 1 to Material 3 theme)
- Tests: JUnit + Android instrumentation test template

No backend service code exists in this repository; the Java Spring Boot backend is external and remains source of truth.

## System architecture

This app is intended to be a separate mobile client:

- Customer app (existing, external to this repository)
- Admin + Delivery app (this repository)
- Shared backend API (external to this repository)

The app consumes backend APIs only, with backend as source of truth for:

- Authentication/session
- Role authorization
- Orders/product/category/customer data
- Delivery lifecycle/assignments
- Notifications and operational events

## Authentication and role model

Expected backend-authenticated roles:

- `ADMIN`
- `DELIVERY_PARTNER`

Role must be returned from backend auth response and enforced by backend authorization controls.

Android-side defense in depth implemented:

- Root auth gate from centralized `AuthStateStore`
- Destination authorization policy (`AuthorizationPolicy`)
- Runtime navigation guard (`NavigationGuard`) against unauthorized/unknown destinations
- Session clear + root reset on logout and invalid session

## Root navigation architecture (Phase 6)

Navigation technology: **XML Navigation Component**.

Root graph structure:

- `splashFragment` (startup gate)
- `authGraph`
  - `loginFragment`
  - `unsupportedRoleFragment` (access restricted)
- `adminGraph`
  - `adminDashboardFragment`
  - `adminOrdersFragment`
  - `adminOrderDetailsFragment`
  - `adminDeliveryPartnersFragment`
  - `adminDeliveryPartnerDetailsFragment`
  - admin feature placeholder destinations for future phases
- `deliveryGraph`
  - `deliveryDashboardPlaceholderFragment`
  - delivery feature placeholder destinations for future phases

Auth state to root destination mapping:

- `Loading` -> `splashFragment`
- `Unauthenticated` / `AuthenticationError` -> `loginFragment`
- `AuthenticatedAdmin` -> `adminGraph`
- `AuthenticatedDeliveryPartner` -> `deliveryGraph`
- `UnsupportedRole` -> `unsupportedRoleFragment`

## Protected navigation and role isolation

`AppDestination` defines destination scope:

- `PUBLIC`
- `ADMIN`
- `DELIVERY`

`AuthorizationPolicy.canAccess(role, destination)` enforces:

- `ADMIN` can access only `ADMIN` + `PUBLIC`
- `DELIVERY_PARTNER` can access only `DELIVERY` + `PUBLIC`
- Unauthenticated users can access only `PUBLIC`

`NavigationGuard` validates every destination change and redirects unauthorized or unknown routes to a safe auth-state destination.

This also protects planned deep-link entry points at runtime (if a deep link resolves to a protected destination without valid role/state, user is redirected safely).

## Session and logout behavior

- `SessionManager` restores persisted encrypted session on startup.
- Auth gate routes to role root only after session restoration is resolved.
- Logout clears stored session and transitions state to `Unauthenticated`.
- Navigation back stack is reset to prevent returning to protected screens after logout.

## Session expiration behavior

- Architecture supports transition to `Unauthenticated` on refresh/session failure.
- If backend refresh succeeds, authenticated state remains intact.
- If refresh fails, session is cleared and routing returns to login.

Exact refresh contract remains backend-dependent and is intentionally isolated behind auth interfaces.

## Admin dashboard architecture (Phase 4 foundation)

Implemented stack:

- `AdminDashboardScreen` (UI)
- `AdminDashboardViewModel`
- `GetAdminDashboardUseCase`
- `AdminDashboardRepository`
- `AdminDashboardRemoteDataSource`
- `AdminDashboardApi`

Data/state behavior:

- Dashboard state is modeled as `Loading`, `Success`, `Empty`, `Error`, and `Unavailable`.
- Pull-to-refresh and retry are ViewModel-driven and de-duplicated to prevent concurrent dashboard requests.
- Unauthorized dashboard failures emit a session-expired event and reuse centralized auth logout/routing behavior.
- Number and currency formatting are centralized in `ValueFormatter` (Indian locale formatting).

Contract strategy:

- Dashboard endpoint path is contract-gated by `AdminDashboardContract`.
- Current implementation uses `PendingBackendAdminDashboardContract`, so no guessed endpoint is called.
- UI shows a production-safe unavailable state when backend contract is missing.

## Admin order management architecture (Phase 5)

Implemented stack:

- `AdminOrdersScreen` (orders list/search/filter/pagination/refresh)
- `AdminOrderDetailsScreen` (order detail/timeline/payment/customer/delivery/status actions)
- `AdminOrdersViewModel`
- `AdminOrderDetailsViewModel`
- `GetAdminOrdersUseCase`
- `GetAdminOrderDetailsUseCase`
- `UpdateAdminOrderStatusUseCase`
- `CancelAdminOrderUseCase`
- `AdminOrdersRepository`
- `AdminOrdersRemoteDataSource`
- `AdminOrdersApi`

Data flow:

- UI -> ViewModel -> Use Case -> Repository -> Remote Data Source -> Retrofit API -> backend.
- Search and filters are sent through query contracts (no large local dataset filtering).
- Pagination is backend-driven and guarded against duplicate page requests.
- Order details load on demand only when an order is selected.

State and error handling:

- Orders state: `Loading`, `Success`, `Empty`, `Error`, `Unavailable`.
- Details state: `Loading`, `Success`, `Error`, `Unavailable`.
- Pull-to-refresh and retry are lifecycle-safe and ViewModel-driven.
- Unauthorized failures trigger centralized session-expiration handling.
- Contract-missing failures render explicit unavailable UI instead of fake order data.

Contract strategy:

- `AdminOrdersContract` defines order-list/details/status-update/cancel request contracts.
- Current wiring uses `PendingBackendAdminOrdersContract`; APIs stay blocked until backend contracts are confirmed.

## Admin delivery partner management architecture (Phase 6)

Implemented stack:

- `DeliveryPartnerListScreen` and `DeliveryPartnerDetailsScreen`
- `DeliveryPartnerListViewModel` and `DeliveryPartnerDetailsViewModel`
- list, details, and update use cases
- `DeliveryPartnerRepository`
- `DeliveryPartnerRemoteDataSource`
- `DeliveryPartnerApi`

Data flow and behavior:

- UI -> ViewModel -> Use Case -> Repository -> Remote Data Source -> Retrofit API -> backend.
- Search is debounced and sent to the backend with the selected account, verification, and availability filters.
- Pagination, refresh, empty, error, and contract-unavailable states are modeled explicitly.
- Details expose profile, account, availability, vehicle, statistics, current orders, and recent history only from backend response data.
- Availability is read-only. Assignment eligibility uses the nullable backend `isAssignable` value and is never calculated locally.
- Admin mutation buttons come only from backend-provided `allowedActions`. All mutations require confirmation and reject duplicate submissions.
- A `409 Conflict` surfaces the mapped backend error and refreshes partner details.

Partner state model:

- Account: `PENDING`, `ACTIVE`, `INACTIVE`, `SUSPENDED`, `REJECTED`
- Verification: `PENDING`, `VERIFIED`, `REJECTED`
- Availability: `ONLINE`, `OFFLINE`
- Work state: `AVAILABLE`, `BUSY`
- Admin actions: `VERIFY`, `REJECT`, `ACTIVATE`, `DEACTIVATE`, `SUSPEND`, `REACTIVATE`
- Unknown backend values map to explicit `UNKNOWN` UI states rather than guessed behavior.

Order relationship:

- Partner details current orders reuse the Phase 5 Admin Order Details destination.
- Order details can open the shared partner details destination when the backend supplies `partnerId`.
- Both destinations remain ADMIN-only through the central authorization policy.

Contract strategy:

- `DeliveryPartnerContract` isolates list/detail/action paths, list query keys, and mutation body fields.
- Current wiring uses `PendingBackendDeliveryPartnerContract`, so no unverified endpoint or payload is sent.
- The UI presents a production-safe unavailable state until the external backend contract is confirmed.

## Admin product management architecture (Phase 7)

Implemented stack:

- `ProductListScreen`, `ProductDetailsScreen`, and `ProductFormScreen` (create/edit)
- `ProductListViewModel`, `ProductDetailsViewModel`, `ProductFormViewModel`
- list, details, category-lookup, create, update, and admin-action use cases
- `ProductManagementRepository` / `ProductManagementRepositoryImpl`
- `ProductManagementRemoteDataSource` / `ProductManagementRemoteDataSourceImpl`
- `ProductManagementApi`
- `ProductManagementContract` / `PendingBackendProductManagementContract`

Data flow and behavior:

- UI -> ViewModel -> Use Case -> Repository -> Remote Data Source -> Retrofit API -> backend, matching the Phase 5/6 layering exactly.
- Product list search is debounced and sent to the backend together with the selected status and category filters and sort order.
- Pagination, refresh (preserving current search/filter/sort criteria), duplicate-request guards, empty, error, and contract-unavailable states are modeled explicitly, mirroring `AdminOrdersViewModel`/`DeliveryPartnerListViewModel`.
- Product details expose only backend-supplied optional fields (description, category, price, discounted price, discount percent, stock, SKU, unit, timestamps). Admin action buttons (`ACTIVATE`, `DEACTIVATE`, `DELETE`, `EDIT`) are rendered only from the backend-provided `allowedActions` list; the app never infers valid transitions locally.
- Activate/deactivate/delete require confirmation and reject duplicate in-flight submissions. A `409 Conflict` on an action refreshes product details instead of trusting stale local state. `EDIT` navigates to the product form in edit mode.
- The product form supports both create and edit modes from one `ProductFormViewModel`/`ProductFormScreen`. Edit mode loads existing product details to prefill fields; both modes load category options from the repository for the category selector. Local baseline validation enforces non-empty name, a selected category, a non-negative decimal price, an optional discount between 0 and 100, and an optional non-negative integer stock — but the backend remains authoritative once its contract is confirmed. The form tracks a dirty flag, blocks duplicate save submissions, and asks for confirmation before discarding unsaved changes on back navigation (both the toolbar back button and the system back gesture).
- Monetary values are modeled as `BigDecimal`/exact decimal strings end-to-end; the app never computes discounted or final prices client-side.

Product/category state model:

- Status: `ACTIVE`, `INACTIVE`, `DRAFT`, `OUT_OF_STOCK` (+ `UNKNOWN` fallback)
- Availability: `IN_STOCK`, `LOW_STOCK`, `OUT_OF_STOCK` (+ `UNKNOWN` fallback)
- Admin actions: `ACTIVATE`, `DEACTIVATE`, `DELETE`, `EDIT`
- Unknown backend values map to explicit `UNKNOWN` states rather than guessed behavior, consistent with `OrderStatus`/`PartnerAccountStatus`.

Category lookup (not Category CRUD):

- `GetProductCategoryOptionsUseCase` / `ProductManagementRepository.getCategoryOptions()` exposes only an id+name lookup for the product form's category selector.
- Full category create/read/update/delete management is explicitly out of scope for this phase (reserved for a future Phase 8) and is not implemented here.

Image handling:

- `ProductSummary`/`ProductDetails` model an optional `imageUrl`, but no image-loading library (Glide/Coil/Picasso/etc.) is present in this project and none was added.
- The list card and details screen always render a static placeholder icon with an explicit "Product image unavailable" content description instead of attempting to load `imageUrl`, because the image storage/CDN contract is unconfirmed.

Contract strategy:

- `ProductManagementContract` isolates list/details/category/create/update/action paths, list query keys, and mutation body fields.
- Current wiring uses `PendingBackendProductManagementContract`, so no unverified endpoint, query key, or payload is ever sent; the remote data source returns a `CONTRACT_MISSING` failure before any network call is attempted.
- The UI presents a production-safe unavailable state (list, details, and form) until the external backend contract is confirmed.

Navigation:

- Replaced the `adminProductsPlaceholderFragment` placeholder with real `adminProductsFragment`, `adminProductDetailsFragment`, and `adminProductFormFragment` destinations in `adminGraph`.
- All three destinations are ADMIN-only via `AppDestination`/`AuthorizationPolicy`/`NavigationGuard`; DELIVERY_PARTNER navigation attempts are redirected exactly like the Phase 5/6 admin destinations.
- The Admin Dashboard's Products quick action now opens the real product list instead of the placeholder.

## Delivery and order lifecycle target (domain constants for later phases)

Order status domain to centralize in app:

- `PENDING`
- `CONFIRMED`
- `PREPARING`
- `READY`
- `ASSIGNED`
- `ACCEPTED`
- `PICKED_UP`
- `OUT_FOR_DELIVERY`
- `DELIVERED`
- `CANCELLED`

Delivery lifecycle:

`READY -> ASSIGNED -> ACCEPTED -> PICKED_UP -> OUT_FOR_DELIVERY -> DELIVERED`

Detailed implementation progress is tracked in `docs/IMPLEMENTATION_STATUS.md`.

### UI/UX design system
Phase 24 introduces reusable Material 3 presentation styles (`NexaMartCard`, `NexaMartOutlinedCard`, `NexaMartPrimaryButton`, `NexaMartOutlinedButton`) and a light/dark semantic color system. These are presentation-only and do not change domain, repository, or API contracts.
