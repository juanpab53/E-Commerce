# Architecture

## Purpose

Defines the target software architecture of the project, split by side: the backend follows a hybrid Domain-Driven Design (DDD) approach on a single Spring Boot module; the frontend architecture is stubbed here and will be defined with the frontend development.

This document is part of the versioned technical documentation under `documentation/`.

## Backend: architecture and design

### Design approach: hybrid DDD

- **Pure aggregate**: `Order` (with `OrderItem`) is the only pure domain aggregate. It has no JPA annotations and no Spring dependencies, protecting its invariants (total, stock, state transitions).
- **Direct JPA entities**: `Product`, `Category`, `User`, and `Payment` are JPA entities that live in the domain layer.
- **One module**: a single Maven module with packages organized by bounded context.
- **Synchronous events**: domain events are published synchronously within the same transaction via a `DomainEventPublisher`, keeping stock updates atomic.

### Bounded contexts

- `identity`: users, roles, and JWT authentication.
- `catalog`: products, categories, and stock.
- `orders`: the `Order` aggregate, its state machine, and its events.
- `payments`: payments and the payment gateway port.
- `shared`: cross-cutting domain and infrastructure (events, config, web).

### Target package structure

```
com.ecommerce
├── PruebaECommerceApplication.java
├── shared/
│   ├── domain/ DomainEvent, DomainException, NotFoundException,
│   │   └── BusinessRuleException, valueobject/ Money, Email, Address
│   └── infrastructure/ events/ SpringDomainEventPublisher,
│       └── config/ SecurityConfig, OpenApiConfig,
│       └── web/ GlobalExceptionHandler, ErrorResponseDTO
├── identity/
│   ├── domain/ User, Role, UserRepository (port)
│   ├── application/ RegisterUserUseCase, LoginUseCase, UserQueryService, dto/
│   ├── infrastructure/ persistence/ UserJpaRepository, UserRepositoryAdapter,
│   │   └── security/ JwtService, JwtAuthenticationFilter, UserDetailsServiceImpl
│   └── interface/web/ AuthController, UserController
├── catalog/
│   ├── domain/ Product, Category, ProductRepository, CategoryRepository,
│   │   └── service/ StockService
│   ├── application/ ProductService, CategoryService, dto/
│   ├── infrastructure/persistence/ (adapters)
│   └── interface/web/ ProductController, CategoryController
├── orders/
│   ├── domain/ Order (pure aggregate), OrderItem, OrderStatus,
│   │   ├── event/ OrderCreatedEvent, OrderCancelledEvent, OrderPaidEvent,
│   │   ├── OrderRepository (port), OrderFactory
│   ├── application/ CreateOrderUseCase, CancelOrderUseCase,
│   │   ├── ChangeOrderStatusUseCase, MarkOrderAsPaidUseCase,
│   │   ├── OrderQueryService, dto/
│   ├── infrastructure/ persistence/ OrderJpaEntity, OrderItemJpaEntity,
│   │   └── OrderJpaRepository, OrderRepositoryAdapter, OrderMapper,
│   │   └── event/ StockRefundListener
│   └── interface/web/ OrderController
└── payments/
    ├── domain/ Payment, PaymentMethod, PaymentGateway (port),
    │   ├── PaymentRepository (port), event/ PaymentProcessedEvent
    ├── application/ ProcessPaymentUseCase, PaymentQueryService, dto/
    ├── infrastructure/ persistence/ PaymentJpaRepository, PaymentRepositoryAdapter,
    │   └── payment/ StripePaymentGateway, LocalPaymentGateway
    └── interface/web/ PaymentController
```

### Design patterns

| Pattern | Where | Problem solved |
|---|---|---|
| Aggregate | `Order` + `OrderItem` | Encapsulates invariants: total, stock, transitions |
| State | `OrderStatus` | Validates valid transitions (currently transitions are skipped) |
| Factory | `OrderFactory` | Centralizes creation invariants |
| Value Object | `Money`, `Email`, `Address` | Removes unsafe `double` for money |
| Domain Events | events in `orders`/`payments` | Decouples side effects from the aggregate |
| Application Service (Use Case) | `application` layer | Orchestrates use cases; transaction only here |
| Repository (port + adapter) | all contexts | Isolates persistence from the domain |
| Ports & Adapters | repositories, `PaymentGateway`, `EventPublisher` | Swap infrastructure without touching the domain |
| Strategy | `StripePaymentGateway` / `LocalPaymentGateway` | Interchangeable payments by profile |
| Anti-Corruption Layer | `OrderMapper` | Maps pure aggregate to/from JPA entity |
| Central mapper | MapStruct | Removes duplicated mappings |

### Transaction rule

- `@Transactional` is allowed only in write use cases (application layer).
- Queries never open a transaction.

### Concurrency

- Optimistic locking with `@Version` on `Product` and `Order`.
- A concurrency conflict surfaces as HTTP `409 Conflict` (see `Api-Contract.md`).

### Project boundaries (out of scope)

- No CQRS, Event Sourcing, Sagas, or projections/read models.
- No microservices; no multi-module Maven.
- No OAuth2 or social login; plain JWT is kept.
- No Stripe webhooks (the port stays ready to add them later).
- No asynchronous events or Outbox (documented as future evolution).
- No Redis/cache, queues, or brokers.
- No frontend.
- No migration of historical business data (schema and enum values only, via Flyway).

## Frontend: architecture (to define)

Space reserved for the frontend architecture. The base is a React 19 + Vite template. Pending decisions to record here:

- Folder structure (e.g., `src/pages`, `src/components`, `src/hooks`, `src/services`, `src/types`).
- State management strategy (global vs. server-state).
- Data layer: API client that consumes the contract defined in `Api-Contract.md`.
- Routing and layout structure.
