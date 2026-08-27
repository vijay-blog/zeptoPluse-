# ZeproPluse Operations Frontend

A Vite + React + TypeScript foundation for the ZeproPluse operations console. It is intentionally independent of the customer app and backend.

## Run locally

```bash
npm install
npm run dev
```

Create `.env.local` from `.env.example` to point the client at another backend:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

Use `npm run build` to type-check and create the production bundle.

## Architecture

- `src/App.tsx` owns page state, loading, and the high-level operations data flow.
- `src/layouts/` contains the application shell and navigation.
- `src/pages/` contains the Dashboard, Products, Categories, and Orders screen modules.
- `src/components/` contains reusable presentational UI components.
- `src/models/` is the shared typed domain-model boundary.
- `src/services/` centralizes API configuration, fetch behavior, and common paged/list response handling.

The current screens are Dashboard, Products, Categories, and Orders. The dashboard aggregates the records already fetched for the three resource screens.

## API integration

The client calls these resources below `VITE_API_BASE_URL`:

- `GET /products`
- `GET /categories`
- `GET /orders?customerId=`

Collection responses may be a bare array or contain one of `content`, `data`, `products`, `categories`, or `orders`. Add request authentication, mutation methods, stricter response DTOs, and server-side paging in `src/api/client.ts` as those backend contracts are introduced.

## Future modules

Recommended next modules are inventory and stock adjustments, fulfilment and delivery workflows, promotions, customer support, reporting, role-based permissions, and audit activity. Keep each domain in a dedicated `src/features/<domain>` folder once it has substantial UI and API behavior.
