# Database Conventions

## Purpose

Defines the persistence conventions of the project, split by side. The backend owns the schema and migrations; the frontend data access layer is stubbed and will be completed with the frontend development. See `Architecture.md` for the domain model and `Naming-Conventions.md` for identifier rules.

## Backend: database conventions

### Migrations

- Schema changes go through versioned **Flyway** migrations (SQL files in `src/main/resources/db/migration`, e.g., `V1__initial_schema.sql`).
- Migration files, table names, and columns are in English.
- `spring.jpa.hibernate.ddl-auto=validate` in production (never `update`).
- If production data exists, verify the `V1` migration against the real schema before enabling `validate`.

### Naming

- Tables: `users`, `products`, `categories`, `orders`, `order_items`, `payments`.
- Columns and foreign keys in English (e.g., `user_id`, `order_date`).

### Enums

- Enum values are persisted in English (`PENDING`, `PAID`, `CREDIT_CARD`, ...).
- The migration to English values is applied via Flyway (e.g., `V3`). It affects persisted data, so test assertions must be aligned.

### Concurrency

- Optimistic locking with `@Version` on `Product` and `Order`.
- A conflict surfaces as HTTP `409 Conflict` (see `Api-Contract.md`).

### Data types

- Money is `BigDecimal` (not `double`). Migrated via Flyway `V2`; test assertions must be adjusted.

### Performance

- The N+1 problem of orders is resolved with `EntityGraph` / fetch join.

## Frontend: data access (to define)

Space reserved for the frontend data-access conventions. Pending items to record here:

- API client that consumes the contract defined in `Api-Contract.md`.
- Server-state strategy (fetching, caching, invalidation).
- Offline behavior and error states (see `Error-Handling.md`).
