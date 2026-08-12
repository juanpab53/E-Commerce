# Definition of Done

## Purpose

Checklist that gates every sprint of the refactor. It references the convention documents instead of repeating their details. A sprint is done only when all items below pass. The backend items are defined now; the frontend items are stubbed and will be completed with the frontend development.

## Backend

### Code

- [ ] No field injection (`@Autowired` on fields); constructor injection only, ideally `final` (see `Architecture.md`).
- [ ] No duplicated manual mappings; central MapStruct mappers (see `Technologies.md`).
- [ ] Business exceptions decoupled from Spring, derived from `DomainException` (see `Error-Handling.md`).
- [ ] `@Transactional` only in write use cases; queries without transaction (see `Architecture.md`).
- [ ] All identifiers, comments, and internal messages in English, with the required casing (see `Naming-Conventions.md`).
- [ ] Checkstyle integrated into the build (`mvn verify`) and CI with no violations.
- [ ] No `import *` and no compiler warnings in the diff.

### Tests

- [ ] `mvn clean verify` green (see `Testing.md`).
- [ ] Each use case has a happy path and its main exceptions.
- [ ] Pure domain tests (`Order`, `OrderStatus`, `Money`) without Spring/H2.
- [ ] Coverage ≥ 80% lines per module (JaCoCo/Codecov).
- [ ] Event tests: events published and the stock listener restores stock.

### Database

- [ ] Versioned Flyway migrations in English; `ddl-auto=validate` (not `update`) in production (see `Database.md`).
- [ ] `@Version` on `Product` and `Order`; conflict → HTTP 409.
- [ ] Enum values in the database in English (`PENDING`, `CREDIT_CARD`, ...) via migration.
- [ ] Dates typed `TIMESTAMP`/`LocalDateTime` (not `String`) in `orders` and `payments`.
- [ ] `UNIQUE(order_id)` in `payments`; `UNIQUE(pedido_id, producto_id)` in `order_items` (quantities merged in `OrderFactory`).
- [ ] Indexes on FKs (`orders.user_id`, `order_items.pedido_id`, `order_items.producto_id`, `payments.order_id`).
- [ ] `created_at`/`updated_at` in `orders` and `payments`.
- [ ] N+1 of orders resolved (EntityGraph / fetch join).

### Security

- [ ] Stateless session with JWT; explicit public routes (see `Security.md` and `Api-Contract.md`).
- [ ] Passwords with `BCryptPasswordEncoder`; never in responses.

### Integrations

- [ ] Stripe through the `PaymentGateway` port; adapters selectable by profile.
- [ ] No credentials in the repository (env vars: `STRIPE_SECRET_KEY`, etc.).
- [ ] `LocalPaymentGateway` by default in dev and CI.

## Frontend (to define)

Space reserved for the frontend definition of done. Pending items to record here:

- [ ] Lint with `camelcase` and tests wired into the frontend CI workflow (see `Testing.md` and `Technologies.md`).
- [ ] JWT handling and route guards implemented (see `Security.md`).
- [ ] Error states handled with the `ErrorResponseDTO` shape (see `Error-Handling.md`).
- [ ] Consumes the backend contract in `Api-Contract.md`.

## Workflow

- [ ] One commit per issue with the `#NNN` prefix, in English (see `Git-Workflow.md`).
- [ ] App compiling and tests green at the close of each sprint.
