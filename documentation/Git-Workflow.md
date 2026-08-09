# Git Workflow

## Purpose

Defines the version-control conventions of the project: branching model, branch naming, commit messages, release cadence, and language policy. These rules keep history clean, traceable, and aligned with the sprint-based refactor.

## Branching model: GitFlow

`feature/sprint-N-*` → `develop` → `release/1.0.0` → `main`

## Sprint branches

| Branch | Sprint |
|---|---|
| `feature/sprint-0-foundations` | 0 — Foundations |
| `feature/sprint-1-identity` | 1 — Identity |
| `feature/sprint-2-catalog` | 2 — Catalog |
| `feature/sprint-3-orders` | 3 — Orders |
| `feature/sprint-4-payments` | 4 — Payments |
| `feature/sprint-5-cleanup` | 5 — Cleanup and closure |

Rules:

- Each sprint branch is created from the updated `develop` (sprint N+1 already contains N).
- Each sprint branch is merged into `develop` with `--no-ff` at closure and then deleted.
- `main` and `develop` are always releasable/buildable.

## Commits

- One commit per issue, with the issue id as prefix: `#001 Rename root package to com.ecommerce`.
- Commit messages in English.
- Each sprint closes with the app compiling and the tests green.

## Releases and hotfixes

- Single release at the end of the refactor: `release/1.0.0` created from `develop`, merged to `main` with tag `v1.0.0`, and merged back to `develop`.
- `hotfix/*` branches are created from `main` only for production bugs and are merged to both `main` and `develop`.

## Language policy

- **Code, branches, and commits**: English.
- **Versioned technical documentation** (`documentation/*.md`): English.
- **Development roadmap** (`PLAN_REFACTOR_BACKEND.md`): Spanish, kept local and not versioned.
