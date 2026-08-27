# ZeptoPluse Backend

Java 17 / Spring Boot 3 modular-monolith API under `com.zeptopluse` for the Flutter ZeptoPluse marketplace app.

## Run

1. Make MySQL 8+ available.
2. Configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` (local `zeptopluse` defaults are supplied).
3. With JDK 17+, run `mvn spring-boot:run`.

Flyway owns the schema. Hibernate is set to `validate`, so it cannot silently change a production schema.

## API

All endpoints are under `/api/v1`:

- `GET /categories`
- `GET /products`, `GET /products/{id}`, `GET /products/category/{categoryId}`, `GET /products/search?query=`
- `POST /customers`, `GET /customers/{id}`
- `POST|GET /customers/{customerId}/addresses`
- `PUT|DELETE /addresses/{id}`
- `POST /orders`, `GET /orders?customerId=`, `GET /orders/{id}`
- `POST /orders/{id}/cancel`
- `POST /payments/create-order`, `POST /payments/verify`, `GET /payments/{id}`

Customer creation accepts either `phone` or Flutter's `mobile`; absent guest names become `Guest Customer`. Product responses retain Flutter-oriented aliases (`imageAsset`, `available`) alongside canonical `imageUrl` and `availability`.

## Data model and checkout

`V1__create_marketplace_schema.sql` creates `customers`, `categories`, `products`, `product_images`, `addresses`, `orders`, and `order_items`, plus future fulfilment foundations: `partner_inventory`, `delivery_partners`, and `order_assignments`. Products record brand, MRP/selling price/discount, presentation metadata, availability, inventory, delivery type, and audit timestamps. Orders retain monetary breakdowns, address and item snapshots, status, payment status, and audit timestamps.

`V2__seed_catalogue.sql` supplies all 31 catalogue categories from Grocery through Other, exactly 100 active products over major categories, and one `product_images` row per product. Image paths are Flutter-style local assets.

Checkout is transactional: product rows are pessimistically locked, duplicate lines are rejected, address ownership/current availability/inventory are checked, prices are always read from `sellingPrice`, inventory is reduced, and order snapshots are persisted atomically. Client totals and prices are never accepted.

COD orders are confirmed server-side without Razorpay credentials. Online orders are created as `PAYMENT_PENDING`; the backend creates Razorpay orders with `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET`, verifies the Razorpay HMAC signature, and only then marks the order payment as `CAPTURED`.
