# Agents

# Tech details

## Stack

- Java 21
- Ktor + Exposed + Flyway
- PostgreSQL
- Auth with Firebase (Future)

## Architecture

- Modular monolith with layered architecture
- Modules are organized by feature/domain (e.g., `lancamento`, `conta`, `usuario`)
- Each module has its own package and contains all related code (models, services, controllers)
- Shared code (e.g., utils, common models) goes in a `common` module
- API layer (controllers) is separate from service layer (business logic) and data access layer (repositories)
- Use of Ktor dependency injection to manage dependencies between layers and modules
- Base package is `br.dev.brunorsch.ledger` for all code, with subpackages for each module and layer
- Project implementations under `src/main/kotlin/ledger` (Package prefix not included), configs under 
`src/main/kotlin/config`

## Communication

- Act as an experienced engineer pair programming with the user
- User usually is Brazilian, so conversation and code can be in Portuguese or English depending on context, reasoning
  should be in English.
- Prefer functional code over explanation
- If a decision is needed, ask directly
- Implement, run lints, try to build — period. Only run tests (automated or functional) if explicitly asked
- If using Copilot, Cursor, or other IDE-integrated agent: don't run anything — human runs, checks results, and reports
  failures

## Golden Rules

1. **Always read existing code** before making changes
2. **Follow project patterns** (naming, structure, imports)
3. **Automated tests are mandatory** if the project already has tests for the feature
4. **Atomic commits** — one feature per commit
5. **Don't break the build** — run checks before finishing
6. **Don't run anything functional** — lints and builds are fair game; tests and runtime are the human's job
7. **Always use BigDecimal for monetary values** — avoid precision errors

## Workflow

### Small tasks (< 30 min)

1. Understand what needs to be done
2. Implement directly
3. Verify (lint/build)
4. Commit if needed

### Large tasks

1. Create a plan at `PLAN.md` and wait for approval
2. When approved, execute the plan
3. At each milestone, validate direction
4. Do a general review at the end

## Commits

```
type(scope): short subject
```

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `ai`
> `ai` type for changes on instructions, memory files, or other agent-related files

Scopes: `prefix-module`
Prefix: `web`, `api` (Omit if on both)
Module: Based on the project structure, usually module names are from packages under `ledger` package.
Scope may be omitted if the change is huge, across multiple modules or if the module is not clear, and on both web and
api.

Example:

```
feat: add endpoint to create lancamento
```
