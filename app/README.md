# NexaMart Customer App

Material 3 customer app for Hyderabad's multi-category marketplace.

## Customer journey

Splash → Home → Categories/Product listing/Search → Product detail → Cart →
Address → COD checkout → Success → Orders → Order detail and tracking.

The app also includes guest profile, saved address management, quantity-aware
cart totals, local customer state, and loading/empty/error search states.

## Architecture

```text
lib/
  config/       runtime environment configuration
  core/         theme and HTTP client
  data/         development catalog fallback
  models/       generic Product, CartItem, Address, CustomerOrder
  providers/    customer state
  repositories/ repository contracts and REST implementations
  routes/       named route foundation
  screens/      customer flow
  services/     API and repository implementations
  utils/        shared presentation helpers
  widgets/      reusable marketplace UI
```

Images are local clean illustrations in `assets/images/products/`; no network
image dependency or emoji product imagery is used.

## API base URL configuration

The Flutter app has a single source of truth for backend host configuration in
`lib/core/app_config.dart` via `API_BASE_URL`.

- Default (development emulator): `http://10.0.2.2:8080/api/v1`
- Railway private network (only inside Railway runtime): `http://nexamart.railway.internal/api/v1`
- Public production for real user devices: use your public HTTPS backend domain,
  for example `https://api.nexamart.com/api/v1` (Railway internal hostnames are
  not reachable from Play Store-installed apps on public networks).

## Run

```powershell
flutter pub get
flutter run --dart-define=API_BASE_URL=http://10.0.2.2:8080/api/v1
```

For a physical Android device, replace `10.0.2.2` with the host machine's LAN
IP, or with your public HTTPS API domain for external testing/production.

## Validate

```powershell
flutter clean
flutter pub get
flutter analyze
flutter test
flutter build apk --debug
```

OTP, online payments, maps, and partner/delivery UIs are intentionally outside
this customer-and-COD phase.
