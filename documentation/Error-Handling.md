# Error Handling

## Purpose

Defines how the API reports errors: the domain exception hierarchy, the global handler, and the uniform response shape. HTTP status semantics are in `Api-Contract.md`; the exception mapping below complements it. The backend owns the error model; the frontend section is stubbed for future completion.

## Backend: error handling

### Domain exceptions (decoupled from Spring)

- `DomainException`: unchecked base class with a `message`.
- `NotFoundException`: entity/aggregate not found.
- `BusinessRuleException`: an invariant or business rule was violated.
- These are plain Java POJOs in `shared/domain` with no Spring annotations, so the domain does not depend on the framework.

### Global handler

- `GlobalExceptionHandler` (`@RestControllerAdvice`) catches domain and framework exceptions and converts them into the uniform response.
- `ErrorResponseDTO`: `{ timestamp, status, error, message, path }`.

### Exception → HTTP mapping

| Exception | HTTP status |
|---|---|
| `NotFoundException` | 404 |
| `BusinessRuleException` | 400 |
| `OptimisticLockingFailureException` (`@Version`) | 409 |
| Authentication/authorization failures (security layer) | 401 / 403 |
| Unexpected errors | 500 (default) |

## Frontend: error handling (to define)

Space reserved for the frontend error-handling conventions. Pending items to record here:

- Parse the uniform `ErrorResponseDTO` shape.
- Display error messages to the user (forms, toasts, inline).
- Redirect or invalidate session on `401`/`403` (see `Security.md`).
