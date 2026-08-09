# Naming Conventions

## Purpose

Defines the single-language and naming rules for all code and internal messages of the system, split by side. The backend rules are authoritative and enforced by tooling; the frontend rules are stubbed and will be completed with the frontend development.

## Backend: naming conventions

### Single language: English

All code and internal system messages are written in English:

- **Identifiers**: classes, interfaces, enums, records, variables, methods, fields, parameters, and packages.
- **Exception messages** and **logs**.
- **Comments** and Javadoc.
- **Flyway migrations**: table names, columns, and SQL files.
- **API routes and endpoints**.

Spanish is reserved for project documentation that is not versioned (e.g., the roadmap `PLAN_REFACTOR_BACKEND.md`). Versioned technical documentation is also in English (see `Git-Workflow.md`). This removes Spanglish and keeps a single language in the codebase.

### Naming cases

- **camelCase**: variables, methods, fields, and parameters (`createOrder`, `stockQuantity`).
- **PascalCase**: classes, interfaces, enums, and records (`Order`, `OrderStatus`, `ProductRepository`).
- **UPPER_SNAKE_CASE**: constants and enum values (`PENDING`, `CREDIT_CARD`).
- Descriptive English names without ambiguous abbreviations.
- Endpoints in English, lowercase (`/orders`, `/users/register`).

### Rename table (Spanish → English)

| Before | After | Context |
|---|---|---|
| `pedidos` / `pagos` / `catalogo` | `orders` / `payments` / `catalog` | Package/context |
| `Pedido` / `DetallePedido` | `Order` / `OrderItem` | Classes |
| `Estado` | `OrderStatus` | Enum class |
| `MetodoPago` | `PaymentMethod` | Enum class |
| `Producto` / `Categoria` | `Product` / `Category` | Classes |
| `Usuario` / `Rol` / `Direccion` | `User` / `Role` / `Address` | Classes |
| `Pago` | `Payment` | Class |
| `SeguridadConfig` | `SecurityConfig` | Configuration |
| `PedidoFactory` | `OrderFactory` | Factory |
| `PedidoCreadoEvent` / `PedidoCanceladoEvent` / `PedidoPagadoEvent` | `OrderCreatedEvent` / `OrderCancelledEvent` / `OrderPaidEvent` | Events |
| `PagoProcesadoEvent` | `PaymentProcessedEvent` | Event |
| `CrearPedidoUseCase` / `CancelarPedidoUseCase` / `CambiarEstadoPedidoUseCase` | `CreateOrderUseCase` / `CancelOrderUseCase` / `ChangeOrderStatusUseCase` | Use cases |
| `MarcarPedidoPagadoUseCase` / `ProcesarPagoUseCase` / `RegistroUsuarioUseCase` | `MarkOrderAsPaidUseCase` / `ProcessPaymentUseCase` / `RegisterUserUseCase` | Use cases |
| `PedidoService` / `ProductoService` / `CategoriaService` / `UsuarioService` / `PagoService` | `OrderService` / `ProductService` / `CategoryService` / `UserService` / `PaymentService` | Application |
| `ConsultaPedidoService` / `ConsultaUsuarioService` / `ConsultaPagoService` | `OrderQueryService` / `UserQueryService` / `PaymentQueryService` | Queries |
| `PedidoRepository` / `ProductoRepository` / `CategoriaRepository` / `PagoRepository` / `UsuarioRepository` | `OrderRepository` / `ProductRepository` / `CategoryRepository` / `PaymentRepository` / `UserRepository` | Ports |
| `PedidoJpaRepository` / `PagoJpaRepository` / `UsuarioJpaRepository` | `OrderJpaRepository` / `PaymentJpaRepository` / `UserJpaRepository` | Spring Data |
| `PedidoRepositoryAdapter` / `PagoRepositoryAdapter` / `UsuarioRepositoryAdapter` | `OrderRepositoryAdapter` / `PaymentRepositoryAdapter` / `UserRepositoryAdapter` | Adapters |
| `PedidoJpaEntity` / `DetallePedidoJpaEntity` | `OrderJpaEntity` / `OrderItemJpaEntity` | JPA entities |
| `PedidoMapper` | `OrderMapper` | Mapper |
| `StockDevolucionListener` | `StockRefundListener` | Listener |
| `PedidoController` / `ProductoController` / `CategoriaController` / `UsuarioController` / `PagoController` | `OrderController` / `ProductController` / `CategoryController` / `UserController` / `PaymentController` | Controllers |
| `UsuarioRegistroDTO` / `UsuarioResponseDTO` | `UserRegistrationDTO` / `UserResponseDTO` | DTOs |
| `PedidoDTO` / `PedidoResponseDTO` / `DetallePedidoDTO` | `OrderDTO` / `OrderResponseDTO` / `OrderItemDTO` | DTOs |
| `PagoDTO` / `PagoResponseDTO` | `PaymentDTO` / `PaymentResponseDTO` | DTOs |
| `PENDIENTE, PAGADO, ENVIADO, ENTREGADO, CANCELADO` | `PENDING, PAID, SHIPPED, DELIVERED, CANCELLED` | `OrderStatus` values |
| `TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA_BANCARIA, EFECTIVO, PAYPAL` | `CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, CASH, PAYPAL` | `PaymentMethod` values |
| `CLIENTE` | `CUSTOMER` | `Role` value |
| `usuario, producto, categoria, pedido, detalle_pedido, pago` | `users, products, categories, orders, order_items, payments` | Tables |

### Enforcement

- **Checkstyle** (backend): naming and style rules; fail on violation in `mvn verify` and CI.
- **PMD** (optional, backend): static analysis.
- **Java records** for DTOs and **OpenAPI** as the typed API contract.

## Frontend: naming conventions (to define)

Space reserved for the frontend naming rules. Pending items to record here:

- Variables, functions, and properties in camelCase, enforced with ESLint `camelcase` (see `Technologies.md`).
- Component files and components in PascalCase (JS/JSX).
- Constants in UPPER_SNAKE_CASE.
- Same single-language policy as the backend: English for code and internal messages.
