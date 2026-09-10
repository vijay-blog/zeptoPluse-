# NexaMart Customer Backend

Spring Boot 3.3 / Java 17 service for Railway.

## Railway service settings
- Root Directory: customer-backend
- Build: Dockerfile
- Healthcheck: /api/v1/health
- Port: Railway PORT environment variable

## Required variables
- DB_URL
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET (32+ random characters)

customer service uses the shared NexaMart MySQL schema so customer-created orders are immediately visible to the partner service.
