# Security Conventions

## Purpose

Defines the authentication, authorization, CORS, and secrets conventions, split by side. The backend owns the security implementation; the frontend section is stubbed and will be completed with the frontend development. See `Api-Contract.md` for public routes and HTTP semantics, and `Technologies.md` for the JWT library.

## Backend: security conventions

### Authentication

- Stateless sessions with **JWT** (`SessionCreationPolicy.STATELESS`).
- The `JwtAuthenticationFilter` is registered before `UsernamePasswordAuthenticationFilter`.
- `jwt.secret.key` and `jwt.time.expiration` are provided via environment variables.

### Public routes

Explicitly public (no token required):

- `POST /auth/login`
- `POST /users/register`
- `GET /products/**`
- `GET /categories/**`
- Swagger UI / OpenAPI endpoints

### Passwords

- `BCryptPasswordEncoder` for password hashing.
- Passwords are never included in responses.

### CORS

- A single `CorsConfigurationSource` bean centralizes allowed origins, methods, and headers.
- Allowed origins come from an environment variable.
- `@CrossOrigin` annotations are removed from controllers.

### Secrets and credentials

- No credentials in the repository. Secrets (database, `STRIPE_SECRET_KEY`, JWT key, etc.) are environment variables.
- Payment gateways are selected by profile through the `PaymentGateway` port: `LocalPaymentGateway` by default in dev/CI, `StripePaymentGateway` when configured.

## Frontend: security (to define)

Space reserved for the frontend security conventions. Pending items to record here:

- Storage and handling of the JWT returned by `POST /auth/login` (see `Api-Contract.md`).
- Attaching the token to requests (e.g., `Authorization: Bearer <token>` header).
- Route guards for protected views.
- Handling of `401`/`403` responses and session expiry (see `Error-Handling.md`).
