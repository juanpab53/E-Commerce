# Testing Conventions

## Purpose

Defines how the project is tested, split by side. The backend section is authoritative and part of the definition of done; the frontend section is stubbed and will be completed with the frontend development. See `Architecture.md` for the layered structure under test and `Definition-of-Done.md` for the checklist that gates every sprint.

## Backend: testing conventions

### Test types

- **Use cases**: happy path plus the main exception cases.
- **Pure domain tests** (`Order`, `OrderStatus`, `Money`) without Spring or H2.
- **Persistence tests** with H2 for JPA mappings and repositories.
- **Controller tests** with MockMvc + Mockito.
- **Event tests**: events are published and the stock refund listener restores stock.

### Coverage

- Minimum target coverage per module: ≥ 80% lines, verified with JaCoCo and reported to Codecov.

### Continuous verification

- `mvn clean verify` is green at all times (at the close of every sprint).
- Mappings are centralized with MapStruct, avoiding duplicated mapping tests.

## Frontend: testing (to define)

Space reserved for the frontend testing conventions. Pending items to record here:

- Test runner and library (e.g., Vitest + React Testing Library).
- Minimum coverage threshold and reporting.
- Lint (`camelcase`) and tests wired into the frontend CI workflow (see `Technologies.md`).
