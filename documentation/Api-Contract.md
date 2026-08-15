# API Contract

## Purpose

Defines the HTTP API conventions that prepare the backend for synchronous consumption by the frontend: OpenAPI, routing, status codes, pagination, and health. Error response shapes are defined in `Error-Handling.md`. The backend defines the contract; the frontend consumes it (section stubbed for future completion).

## Backend: API surface

### OpenAPI

- Swagger UI / OpenAPI contract with tags grouped by bounded context.
- DTOs are Java `records` (see `Naming-Conventions.md`).

### Routing conventions

- Endpoints in English and lowercase.
- Main endpoints: `POST /auth/login`, `POST /users/register`, `GET /products`, `GET /categories`, `POST /orders`, `POST /payments`.

### HTTP status codes

| Code | Meaning |
|---|---|
| 200 OK | Successful read/update |
| 201 Created | Resource created |
| 204 No Content | Successful operation without body |
| 400 Bad Request | Business rule violation / input validation |
| 401 Unauthorized | Missing/invalid credentials |
| 403 Forbidden | Insufficient permissions |
| 404 Not Found | Resource not found |
| 409 Conflict | Concurrency conflict (`@Version`) |

- **3xx (redirections)**: intentionally not generated. The API is JSON-only with no
  redirects, and authentication uses `httpBasic` (unauthenticated requests get `401`,
  never a `302` to a login page).
- **5xx**: every unexpected error returns `500` with the uniform `ErrorResponseDTO`
  (generic message, no internal details). `502`/`503`/`504` are emitted by the reverse
  proxy/gateway (nginx), not by the application.

### Pagination

- List endpoints support `?page=&size=`.

### Health check

- `/actuator/health` for orchestrating frontend startup in CI.

### Login contract

- `POST /auth/login` → `LoginResponse { username, token, message }`.

## Frontend: API consumption (to define)

Space reserved for the frontend consumption conventions. Pending items to record here:

- Consume the OpenAPI contract of the backend.
- Handle the uniform `ErrorResponseDTO` shape (see `Error-Handling.md`).
- Pagination params (`page`/`size`) and their UI representation.
- `/actuator/health` as the readiness check when orchestrating the frontend in CI.
