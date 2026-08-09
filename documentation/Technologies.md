# Technologies

## Purpose

Catalog of the technologies used in the project, with versions and purpose, split by side. The backend sections are defined; the frontend section is stubbed and will be completed with the frontend development. See `Architecture.md` for how these pieces fit together.

## Backend: technologies

### Current stack (kept)

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Base language |
| Spring Boot | 4.0.1 | Framework |
| spring-boot-starter-web | managed | REST API |
| spring-boot-starter-data-jpa | managed | Persistence (Hibernate) |
| spring-boot-starter-security | managed | Security (migrates to JWT) |
| spring-boot-starter-validation | managed | DTO validation |
| PostgreSQL Driver | managed | Supabase (Postgres) |
| H2 (test) | managed | In-memory database for tests |
| Lombok | managed | Boilerplate (adds `@RequiredArgsConstructor`) |
| springdoc-openapi-starter-webmvc-ui | 2.8.3 | Swagger UI / OpenAPI |
| spring-boot-starter-test + security-test | managed | JUnit 5, Mockito, AssertJ, MockMvc |
| JaCoCo | 0.8.14 | Coverage → Codecov |
| Maven Wrapper | managed | Reproducible build |

### Stack to add

| Technology | Version | Purpose |
|---|---|---|
| JJWT (api/impl/jackson) | 0.13.0 | Token issuance and validation |
| Flyway (`flyway-core` + `flyway-database-postgresql`) | managed by Boot | Schema migrations |
| Stripe Java SDK (`com.stripe:stripe-java`) | latest stable | Real payment gateway |
| MapStruct (`mapstruct` + processor) | 1.6.x | Central mappers |
| spring-boot-starter-actuator | managed | `/actuator/health` |
| Checkstyle (Maven plugin) | managed by parent | Java style and naming rules; fail on violation in `mvn verify` and CI |
| PMD (plugin, optional) | managed by parent | Static analysis: duplication and bad practices |
| Centralized CORS bean | — | In `SecurityConfig` |
| docker-compose (repo) | — | Local Postgres/backend |

### Typing note

The backend is already statically typed with Java; it is reinforced using **records** for DTOs and **OpenAPI** as the typed contract of the API. The naming and style tool is **Checkstyle**. See `Naming-Conventions.md`.

### Out of scope (not included)

Redis, brokers, microservices, CQRS/Event Sourcing, OAuth2, Stripe webhooks, multi-module Maven, cache, queues, frontend.

## Frontend: technologies (to define)

Space reserved for the frontend stack. The base is a React 19 + Vite template. Pending items to record here:

- Build tool: Vite (already present).
- Language: JavaScript (optional TypeScript).
- Linting: ESLint with the `camelcase` rule (see `Naming-Conventions.md`).
- Testing: to define (e.g., Vitest + React Testing Library).
- HTTP client and state management: to define.
