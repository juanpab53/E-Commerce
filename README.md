# E-Commerce

A full e-commerce platform that includes both a **Backend** (Spring Boot) and a **Frontend** (React), designed to be scalable, secure, and adaptable to different kinds of products.

## Project idea

The repository hosts a complete online-store application: users register and log in, browse products and categories, place orders, and pay through a payment gateway. The system covers the whole lifecycle of an order — from catalog browsing to payment processing — keeping inventory consistent.

The codebase is being refactored into a cleaner architecture: a hybrid Domain-Driven Design approach on the backend (bounded contexts, ports and adapters, a pure `Order` aggregate, and a `PaymentGateway` port with a local implementation by default and Stripe when configured), plus a React frontend. The refactor follows a feature-branch workflow with one commit per issue; the agreed conventions are versioned in `documentation/`.

## Repository contents

- **`/backend`**: REST API with Java 21, Spring Boot 4.x, Spring Data JPA, Spring Security, and OpenAPI.
- **`/frontend`**: modern React 19 + Vite UI (starting point).
- **`/documentation`**: versioned technical conventions (see [Documentation](#documentation)).

## Quick start

### Prerequisites

- JDK 21
- Maven (or use the Maven Wrapper in `backend/`)
- Node.js 18+ and npm

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`:

- Swagger UI / OpenAPI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

To run the tests:

```bash
cd backend
./mvnw test
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The app starts on the URL printed by Vite (default `http://localhost:5173`).

## Documentation

The versioned technical documentation lives in `documentation/`. It is written in English and split into backend (defined) and frontend (to be defined) sections.

| File | Content |
|---|---|
| `Architecture.md` | System design: DDD approach, bounded contexts, package structure, patterns |
| `Technologies.md` | Stack catalog: versions, purpose, what is in and out of scope |
| `Naming-Conventions.md` | Single-language (English) and casing rules for the whole codebase |
| `Git-Workflow.md` | Branching model, commit conventions, and release process |
| `Database.md` | Persistence: migrations, naming, enums, concurrency, data types |
| `Security.md` | Authentication (JWT), public routes, CORS, secrets |
| `Api-Contract.md` | HTTP API: OpenAPI, routes, status codes, pagination, health |
| `Error-Handling.md` | Exception hierarchy and the uniform error response |
| `Testing.md` | Test types, coverage, and continuous verification |
| `Definition-of-Done.md` | Checklist that gates every sprint |

### Recommended reading order

1. `Architecture.md` — understand the system design first.
2. `Technologies.md` — the stack that implements it.
3. `Naming-Conventions.md` — the language and style rules of the code.
4. `Git-Workflow.md` — how changes are delivered.
5. `Database.md` — how persistence works.
6. `Security.md` — how the API is protected.
7. `Api-Contract.md` — the contract the frontend consumes.
8. `Error-Handling.md` — how errors are reported.
9. `Testing.md` — how the code is verified.
10. `Definition-of-Done.md` — the checklist that closes each sprint.
