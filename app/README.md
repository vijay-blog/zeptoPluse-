# NexaMart Customer App

The app is configured by default for the public NexaMart Railway backend:
`https://zeptopluse-production.up.railway.app/api/v1`

For another environment pass `--dart-define=API_BASE_URL=https://host/api/v1`.

The customer catalogue, guest customer record, addresses and orders use the Spring Boot API. Mock catalogue fallback is disabled in production code.
