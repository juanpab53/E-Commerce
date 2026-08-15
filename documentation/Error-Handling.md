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
| `ValidationException` (value objects `Email`/`Money`) | 400 |
| `MethodArgumentNotValidException`/`BindException` (`@Valid`) | 400 |
| `HttpMessageNotReadableException` (malformed JSON) | 400 |
| `MethodArgumentTypeMismatchException` (invalid param type) | 400 |
| `OptimisticLockingFailureException` (`@Version`) | 409 |
| Authentication/authorization failures (security layer) | 401 / 403 |
| Unexpected errors | 500 (generic message, internal details logged) |

### Redirections (3xx)

- 3xx responses are intentionally **not generated**: the API is JSON-only with no
  redirects, and authentication uses `httpBasic` (an unauthenticated request gets
  `401`, never a `302` to a login page).

### Server errors (5xx)

- Every `500` passes through the global handler and uses the uniform `ErrorResponseDTO`;
  the response body is a generic message and the stack trace is logged server-side
  (no internal details are leaked).
- `502`/`503`/`504` are emitted by the reverse proxy/gateway (e.g., nginx), not by the
  application.

## Frontend: error handling (to define)

Space reserved for the frontend error-handling conventions. Pending items to record here:

- Parse the uniform `ErrorResponseDTO` shape.
- Display error messages to the user (forms, toasts, inline).
- Redirect or invalidate session on `401`/`403` (see `Security.md`).
