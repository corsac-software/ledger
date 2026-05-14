# Web ↔ Server Integration

## TL;DR

> **Quick Summary**: Integrate the web frontend (React + IndexedDB) with the server backend (Kotlin/Ktor + PostgreSQL), adopting the server's data model (OrcamentoMensal + LancamentoMensal), removing IndexedDB, replacing the "derived month" approach with explicit monthly budgets, and swapping all hardcoded entities (categories, cards) for server CRUDs.
>
> **Deliverables**:
> - Server endpoint for monthly budget search by year-month
> - Complete API client layer in the web frontend
> - All 6 entity integrations (Categorias, Cartões, Lançamentos Fixos, Parcelamentos, Orçamentos Mensais, Faturas)
> - New API-based state management replacing IndexedDB
> - UI migration for all sections
> - Updated documentation
>
> **Estimated Effort**: Large
> **Parallel Execution**: YES - 5 waves, max 5 concurrent tasks
> **Critical Path**: T2 → T7 → T9 → T11 → T14

---

## Context

### Original Request
Integrar o web (MVP browser) com o server (backend Kotlin/Ktor). A maioria das coisas se resume em plugar os endpoints do back, exceto a parte dos lançamentos do mês que tem divergência conceitual entre o front (mês derivado) e o back (mês explícito com Orçamento Mensal).

### Interview Summary

**Key Discussions**:
- Data model: Adopt server's OrcamentoMensal + LancamentoMensal model (mês explícito)
- Monthly overrides: Use LancamentoMensal edits instead of MonthOverride system
- Auth: Keep hardcoded idUsuario=1, defer Firebase
- Persistence: Online-only, remove IndexedDB
- Categories/Cards: Adopt server entities with CRUD
- mesFim: Use soft delete (excluidoEm) instead of end date field
- Month search: Add server endpoint for ano-mês query
- Post-import: Accept frozen values (imported Lancamentos are independent copies)

**Research Findings**:
- Server has 7 entity groups with complete CRUD APIs
- Frontend has repository pattern (IFinanceRepository) which can be swapped
- Frontend's buildMonthView() derives month from base entities + overrides
- Server auto-imports LancamentoFixos when creating OrcamentoMensal
- `Parcelamento.valor` = per-installment value (consistent with frontend)

### Metis Review

**Identified Gaps** (addressed):
- Auto-import on budget creation: UX designed around this (navigate → GET/POST → auto-import)
- mesFim missing: Use excluidoEm (soft delete) as end date equivalent
- No month search endpoint: Add to server scope
- Parcelamento.valor semantics: Confirmed as per-installment value
- Test strategy: Tests-after + agent QA for each task
- Server-side changes needed: Added endpoint creation to scope

---

## Work Objectives

### Core Objective
Replace the web frontend's offline IndexedDB-based architecture with API calls to the Kotlin/Ktor server, adopting the server's data model throughout.

### Concrete Deliverables
- `GET /api/orcamentos-mensais?anoMes=YYYY-MM` endpoint on the server
- API client layer with types, mappers, and error handling in the web frontend
- API service modules for all 6 entity groups
- New FinanceContext/Provider based on API calls
- Updated UI components for all sections
- Removed IndexedDB/Dexie and MonthOverride system
- Updated tests and documentation

### Definition of Done
- [ ] All server endpoints accessible from frontend without CORS errors
- [ ] Month navigation creates/loads OrcamentoMensal from server
- [ ] All CRUD operations use server APIs (no IndexedDB writes)
- [ ] LancamentoMensal status edits replace MonthOverride functionality
- [ ] Categories and Cards come from server
- [ ] `npm run build` passes with zero errors
- [ ] `npm test` passes (rewritten tests)
- [ ] Dev proxy forwards `/api/*` to server

### Must Have
- Server endpoint for ano-mês budget search
- Complete API client with TypeScript types matching server DTOs
- OrcamentoMensal-based month navigation (replace buildMonthView derivation)
- LancamentoMensal status edits (replace MonthOverride system)
- All 6 entity CRUDs using server APIs
- Online-only behavior (no IndexedDB fallback)
- Updated ARCHITECTURE.md reflecting new architecture
- Updated MEMORY.md on server

### Must NOT Have (Guardrails)
- NO Firebase auth activation (keep idUsuario=1 hardcoded)
- NO IndexedDB caching or offline mode
- NO new features that the server doesn't support
- NO MonthOverride system in the new code (replaced by LancamentoMensal edits)
- NO buildMonthView() derivation (replaced by API-based month view)
- NO data migration tool (out of scope)
- NO leaving old code partially removed (clean removal only after replacement works)

---

## Verification Strategy (MANDATORY)

> **ZERO HUMAN INTERVENTION** - ALL verification is agent-executed.

### Test Decision
- **Infrastructure exists**: YES (vitest)
- **Automated tests**: Tests-after — write tests after implementation
- **Framework**: vitest
- **Existing tests**: 21+ test files, many will need rewriting

### QA Policy
Every task includes agent-executed QA scenarios.
Evidence saved to `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`.

- **API calls**: Use Bash (curl) — send requests, assert status + response fields
- **Frontend**: Use Playwright (playwright skill) — navigate, interact, assert DOM, screenshot
- **Build**: Use Bash — `npm run build`, assert zero errors

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately - foundation + types):
├── T1: Server: Add ano-mês search endpoint [quick]
├── T2: Web: API client + TypeScript types + mappers [quick]
└── T3: Server: Update MEMORY.md [quick]

Wave 2 (After Wave 1 - API services, MAX PARALLEL):
├── T4: Categorias + Cartões API services [unspecified-high]
├── T5: Lançamentos Fixos API service [deep]
├── T6: Parcelamentos API service [unspecified-high]
├── T7: Orçamento Mensal + Lançamentos Mensais API service [deep]
└── T8: Faturas API service [unspecified-high]

Wave 3 (After Wave 2 - state management core):
└── T9: Create new API-based FinanceContext/Provider [deep]

Wave 4 (After Wave 3 - UI migration, PARALLEL):
├── T10: Category + Card settings UI [visual-engineering]
├── T11: Despesas/Receitas sections + Month UI [deep]
├── T12: Parcelamentos + Faturas UI [visual-engineering]
└── T13: Summary + Charts [visual-engineering]

Wave 5 (After Wave 4 - cleanup + quality):
├── T14: Remove old code (IndexedDB, Dexie, MonthOverride, buildMonthView) [unspecified-high]
└── T15: Rewrite tests + update ARCHITECTURE.md [writing]

Wave FINAL (After ALL tasks — 4 parallel reviews):
├── F1: Plan compliance audit (oracle)
├── F2: Code quality review (unspecified-high)
├── F3: Real manual QA (unspecified-high + playwright)
└── F4: Scope fidelity check (deep)
→ Present results → Get explicit user okay

Critical Path: T2 → T7 → T9 → T11 → T14
Parallel Speedup: ~60% faster than sequential
Max Concurrent: 5 (Wave 2)
```

### Dependency Matrix

| Task | Depends On | Blocks | Wave |
|------|-----------|--------|------|
| T1 | — | T7 | 1 |
| T2 | — | T4-T8 | 1 |
| T3 | — | — | 1 |
| T4 | T2 | T10 | 2 |
| T5 | T2 | T11 | 2 |
| T6 | T2 | T12 | 2 |
| T7 | T1, T2 | T9 | 2 |
| T8 | T2 | T12 | 2 |
| T9 | T4-T8 | T10-T13 | 3 |
| T10 | T9 | T14 | 4 |
| T11 | T9 | T14 | 4 |
| T12 | T9 | T14 | 4 |
| T13 | T9 | T14 | 4 |
| T14 | T10-T13 | — | 5 |
| T15 | T14 | F1-F4 | 5 |

### Agent Dispatch Summary

- **Wave 1**: 3 tasks — T1 → `quick`, T2 → `quick`, T3 → `quick`
- **Wave 2**: 5 tasks — T4 → `unspecified-high`, T5 → `deep`, T6 → `unspecified-high`, T7 → `deep`, T8 → `unspecified-high`
- **Wave 3**: 1 task — T9 → `deep`
- **Wave 4**: 4 tasks — T10 → `visual-engineering`, T11 → `deep`, T12 → `visual-engineering`, T13 → `visual-engineering`
- **Wave 5**: 2 tasks — T14 → `unspecified-high`, T15 → `writing`
- **FINAL**: 4 tasks — F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high` + `playwright`, F4 → `deep`

---

## TODOs

- [ ] 1. Server: Add ano-mês search endpoint for OrcamentosMensais

  **What to do**:
  - Add `anoMes` query parameter support to the existing `GET /api/orcamentos-mensais` endpoint
  - When `anoMes` is provided (format `YYYY-MM` or `YYYYMM`), filter results by year-month
  - When `anoMes` is not provided, return all budgets (current behavior preserved)
  - Add corresponding repository method `buscarPorAnoMes(idUsuario: Long, anoMes: AnoMes): OrcamentoMensal?`
  - Add service method that calls repository and returns the budget (or null if not found)
  - Update the route to accept and parse the query parameter
  - Ensure the endpoint returns `200 OK` with the budget or `404 Not Found` if no budget exists for that month

  **Must NOT do**:
  - Do NOT change existing endpoint behavior when `anoMes` is not provided
  - Do NOT add Firebase auth verification
  - Do NOT modify the OrcamentoMensal domain model

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Single endpoint addition with clear pattern to follow from existing routes
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with T2, T3)
  - **Blocks**: T7 (Orçamento Mensal API service)
  - **Blocked By**: None (can start immediately)

  **References** (CRITICAL):

  **Pattern References**:
  - `server/src/main/kotlin/orcamento/mensal/routes/OrcamentosMensaisRoutes.kt:17-176` — Existing route patterns, OpenAPI docs, response schemas
  - `server/src/main/kotlin/orcamento/mensal/api/OrcamentosMensaisController.kt` — Controller pattern for handling requests and responses
  - `server/src/main/kotlin/orcamento/mensal/service/OrcamentosMensaisService.kt:20-44` — Existing service methods (criar, buscarTodos, buscarPorId, excluir)
  - `server/src/main/kotlin/orcamento/mensal/data/repository/OrcamentosMensaisRepository.kt` — Repository pattern for DB access

  **API/Type References**:
  - `server/src/main/kotlin/orcamento/mensal/domain/AnoMes.kt` — AnoMes value object with `parse()` method that accepts `YYYY-MM` format
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/OrcamentosMensais.kt` — DTOs for request/response serialization

  **WHY Each Reference Matters**:
  - Routes file shows the exact pattern for adding query parameters and OpenAPI documentation
  - Controller shows how to extract parameters from calls and respond
  - Service shows the business logic layer pattern
  - AnoMes has a convenient `parse()` that already validates `YYYY-MM` format

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Search budget by year-month returns existing budget
    Tool: Bash (curl)
    Preconditions: Server running on localhost:8080, budget exists for 2026-05
    Steps:
      1. `curl -s http://localhost:8080/api/orcamentos-mensais?anoMes=2026-05`
      2. Assert response status is 200
      3. Assert response body contains orcamento with ano=2026, mes=5
    Expected Result: 200 OK with matching OrcamentoMensalResponse
    Failure Indicators: 404 (no budget), 400 (bad format), empty body
    Evidence: .sisyphus/evidence/task-1-search-by-month-success.json

  Scenario: Search budget for non-existent month returns 404
    Tool: Bash (curl)
    Preconditions: Server running, no budget for 2099-01
    Steps:
      1. `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/orcamentos-mensais?anoMes=2099-01`
      2. Assert status code is 404
    Expected Result: 404 Not Found
    Failure Indicators: 200 with empty array, 500
    Evidence: .sisyphus/evidence/task-1-search-by-month-notfound.txt

  Scenario: List all budgets without anoMes query still works
    Tool: Bash (curl)
    Preconditions: Server running, at least one budget exists
    Steps:
      1. `curl -s http://localhost:8080/api/orcamentos-mensais`
      2. Assert response status is 200
      3. Assert response is an array
    Expected Result: 200 OK with array of all budgets
    Failure Indicators: 400, 500, empty response
    Evidence: .sisyphus/evidence/task-1-list-all.json
  ```

  **Commit**: YES (groups with T1)
  - Message: `feat(api): add ano-mês query param to orcamentos-mensais endpoint`
  - Files: `server/src/main/kotlin/orcamento/mensal/routes/OrcamentosMensaisRoutes.kt`, `server/src/main/kotlin/orcamento/mensal/api/OrcamentosMensaisController.kt`, `server/src/main/kotlin/orcamento/mensal/service/OrcamentosMensaisService.kt`, `server/src/main/kotlin/orcamento/mensal/data/repository/OrcamentosMensaisRepository.kt`
- Files: `web/src/features/finance/lib/api/faturasService.ts`

- [ ] 9. Web: Create new API-based FinanceContext/Provider

  **What to do**:
  - Create a new `web/src/features/finance/context/FinanceApiContext.tsx` that replaces the old FinanceContext with API-backed state management:
    - **Orcamento state**: `currentOrcamento` (OrcamentoMensalResponse | null), `currentMonthKey` (string)
    - **Navigation**: `navigateMonth(step)` — changes currentMonthKey, calls `fetchOrcamentoByAnoMes(anoMes)`, if 404 creates via `createOrcamento(anoMes)` (which auto-imports LancamentoFixos)
    - **Lancamentos Mensais state**: `lancamentos` for current orcamento, loaded from `fetchLancamentos(orcamentoId)`
    - **Lancamentos Fixos state**: `lancamentosFixos` loaded from `fetchLancamentosFixos()`
    - **Categorias state**: `categorias` loaded from `fetchCategorias()`, with `setupCategoriasPadrao()` for first-time
    - **Cartões state**: `cartoes` loaded from `fetchCartoes()`
    - **Parcelamentos state**: Stored per-cartao, loaded on demand
    - **Faturas state**: Stored per-cartao, loaded on demand
    - **Mutation functions**: CRUD wrappers for each entity that call API, then refresh the relevant state
    - **Status tracking**: `statusDespesa` handling — mark as PAGO via `updateLancamento(orcamentoId, lancamentoId, {statusDespesa: "PAGO"})` replaces the old `upsertMonthOverride({type: 'fixedExpensePayment', paid: true})`
  - The new context should follow the same 3-context pattern as the old one (state, derived, actions) for minimal UI disruption
  - Create a `useOrcamentoMensal(anoMes)` hook that handles the fetch-or-create flow for month navigation
  - Remove all references to IndexedDB/Dexie state hydration and persistence
  - Remove the `useHydrateFinanceState` and `usePersistFinanceState` hooks (replace with API calls)

  **Must NOT do**:
  - Do NOT remove the old FinanceContext yet (T14 handles that)
  - Do NOT connect UI components to the new context yet (Wave 4)
  - Do NOT remove buildMonthView or MonthOverride types yet

  **Recommended Agent Profile**:
  - **Category**: `deep`
    - Reason: This is the architectural core — the new FinanceContext replaces the entire state management layer. It must correctly orchestrate API calls, handle loading states, and maintain the same interface the UI expects.
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (sequential after Wave 2)
  - **Blocks**: T10, T11, T12, T13 (all UI tasks)
  - **Blocked By**: T4, T5, T6, T7, T8 (all API services)

  **References** (CRITICAL):

  **Pattern References**:
  - `web/src/features/finance/context/FinanceContext.tsx` — Current 3-context pattern (state, derived, actions) that the new context must match
  - `web/src/features/finance/context/financeContextInternals.ts` — Current hydration and persistence logic (to be replaced with API calls)
  - `web/src/features/finance/domain/stateReducers.ts` — Current mutation logic (to be replaced with API calls + state refresh)
  - `web/src/features/finance/domain/actionFactory.ts` — Current action creators (to be replaced with API-backed actions)

  **API/Type References**:
  - `web/src/features/finance/lib/api/orcamentosMensaisService.ts` — (from T7) OrcamentoMensal API service
  - `web/src/features/finance/lib/api/lancamentosMensaisService.ts` — (from T7) LancamentoMensal API service
  - `web/src/features/finance/lib/api/lancamentosFixosService.ts` — (from T5) LancamentoFixo API service
  - `web/src/features/finance/lib/api/categoriasService.ts` — (from T4) Categorias API service
  - `web/src/features/finance/lib/api/cartoesService.ts` — (from T4) Cartões API service
  - `web/src/features/finance/lib/api/parcelamentosService.ts` — (from T6) Parcelamentos API service
  - `web/src/features/finance/lib/api/faturasService.ts` — (from T8) Faturas API service

  **WHY Each Reference Matters**:
  - FinanceContext.tsx defines the EXACT interface that UI components consume — the new context must expose the same operations (addFixedExpense, addRevenue, etc.) but backed by API calls
  - financeContextInternals.ts shows the hydration/persistence pattern — the new context replaces IndexedDB hydration with API fetches
  - stateReducers.ts shows ALL current mutations — each must have an API equivalent in the new context
  - The 3-context pattern (state/derived/actions) should be preserved to minimize UI changes

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: FinanceApiContext compiles and exports all required state/actions
    Tool: Bash
    Steps:
      1. `cd web && npx tsc --noEmit`
      2. Assert zero type errors referencing FinanceApiContext
      3. Grep for exported providers: FinanceApiStateProvider, FinanceApiDerivedProvider, FinanceApiActionsProvider
      4. Grep for exported hooks: useFinanceApiState, useFinanceApiDerived, useFinanceApiActions
    Expected Result: Clean compilation, all providers and hooks exported
    Evidence: .sisyphus/evidence/task-9-api-context-compiles.txt

  Scenario: Context handles month navigation with create-if-not-found flow
    Tool: Bash
    Preconditions: web project compiles
    Steps:
      1. Grep FinanceApiContext for navigateMonth function
      2. Verify it calls fetchOrcamentoByAnoMes first
      3. Verify it creates orcamento on 404
      4. Verify it fetches lancamentos after loading orcamento
    Expected Result: Month navigation follows fetch → create-if-404 → fetch-lancamentos flow
    Evidence: .sisyphus/evidence/task-9-navigation-flow.txt

  Scenario: Context has CRUD functions for all 6 entity groups
    Tool: Bash
    Steps:
      1. Grep FinanceApiContext for functions matching: addLancamentoFixo, updateLancamentoFixo, deleteLancamentoFixo, addLancamentoMensal, updateLancamentoMensal, deleteLancamentoMensal, addCartao, updateCartao, deleteCartao, addCategoria, updateCategoria, deleteCategoria, addParcelamento, updateParcelamento, deleteParcelamento, addFatura, updateFatura, deleteFatura
      2. Verify all 18+ functions exist
    Expected Result: All CRUD functions present
    Evidence: .sisyphus/evidence/task-9-crud-functions.txt
  ```

  **Commit**: YES (groups with T9)
  - Message: `refactor(web): create API-based FinanceContext/Provider`
  - Files: `web/src/features/finance/context/FinanceApiContext.tsx`, related hooks
  - Pre-commit: `cd web && npm run build`

- [ ] 10. Web: Migrate category and card settings UI to API

  **What to do**:
  - Update the categories UI to use `categoriasService` instead of hardcoded `CATEGORIES` from `ui/constants.ts`
  - Replace `Settings.cardBills` management with `cartoesService` CRUD
  - Update the `Settings` type to remove `cardBills` (now comes from API)
  - Update modals/inputs that reference hardcoded categories to use API-provided categories
  - Ensure the first-time experience calls `setupCategoriasPadrao()` when no categories exist
  - Wire up `useFinanceApiActions()` for category and card mutations

  **Must NOT do**:
  - Do NOT touch despesas, receitas, or lançamentos sections (T11)
  - Do NOT remove hardcoded CATEGORIES from constants.ts yet (can be removed in T14)
  - Do NOT modify buildMonthView or MonthOverride system

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: UI migration component — requires updating visual components to use new data sources
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 4 (with T11, T12, T13)
  - **Blocks**: T14 (cleanup)
  - **Blocked By**: T9 (FinanceApiContext)

  **References**:

  **Pattern References**:
  - `web/src/features/finance/ui/constants.ts` — Current hardcoded CATEGORIES and CARD icons/names
  - `web/src/features/finance/components/sections/` — Section components that use categories
  - `web/src/features/finance/components/modals/` — Modals that create/edit categories or cards
  - `web/src/features/finance/hooks/useFinanceActions.ts` — Current action hook pattern

  **API/Type References**:
  - `web/src/features/finance/lib/api/categoriasService.ts` — (from T4) Category API service
  - `web/src/features/finance/lib/api/cartoesService.ts` — (from T4) Card API service
  - `web/src/features/finance/lib/api/types.ts` — (from T2) CategoriaResponse, CartaoResponse types

  **WHY Each Reference Matters**:
  - ui/constants.ts has the hardcoded categories that must be replaced with API data
  - Section and modal components show exactly which UI elements reference categories and cards
  - The action hook pattern shows how mutations should be connected to the context

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Category CRUD works end-to-end
    Tool: Playwright
    Preconditions: Server running, frontend dev server running
    Steps:
      1. Navigate to the app
      2. Open category management
      3. Create a new category "TestCat" with icon "🔧"
      4. Assert "TestCat" appears in the category list
      5. Edit "TestCat" to "TestCat2"
      6. Assert "TestCat2" appears and "TestCat" is gone
      7. Delete "TestCat2"
      8. Assert "TestCat2" is gone
    Expected Result: All CRUD operations work via API
    Failure Indicators: 404 errors, data not appearing, mutations failing
    Evidence: .sisyphus/evidence/task-10-category-crud.png

  Scenario: Card CRUD works end-to-end
    Tool: Playwright
    Preconditions: Server running, frontend dev server running
    Steps:
      1. Navigate to the app
      2. Open card management or settings
      3. Add a new card "TestCard" with color "#FF0000" and icon "💳"
      4. Assert "TestCard" appears in the card list
      5. Edit "TestCard" to "TestCard Updated"
      6. Assert updated name appears
      7. Delete "TestCard Updated"
      8. Assert card is removed
    Expected Result: All card CRUD works via API
    Failure Indicators: 404 errors, UI not updating
    Evidence: .sisyphus/evidence/task-10-card-crud.png

  Scenario: Default categories setup on first load
    Tool: Playwright
    Preconditions: Server running with no categories for user
    Steps:
      1. When categories list is empty, trigger setup padrão
      2. Assert default categories (CASA, TELEFONE, etc.) are loaded from API
    Expected Result: Default categories created via /categorias/setup
    Failure Indicators: Empty category list, setup not called
    Evidence: .sisyphus/evidence/task-10-default-categories.png
  ```

  **Commit**: YES (groups with T10)
  - Message: `feat(web): migrate category and card settings UI to API`
  - Files: Updated components in `web/src/features/finance/components/`, `web/src/features/finance/ui/constants.ts`

- [ ] 11. Web: Migrate despesas/receitas sections and month navigation to API

  **What to do**:
  - Replace `buildMonthView()` derivation with `fetchLancamentos(orcamentoId)` for the current month
  - Replace `MonthNav` component to use `navigateMonth()` from FinanceApiContext
    - Navigating to a new month: if no orcamento exists, call `createOrcamento(anoMes)` (which auto-imports LancamentoFixos), then fetch lancamentos
  - Update Fixed Expenses section:
    - Replace `addFixedExpense` → `createLancamentoFixo({tipo: 'DESPESA', ...})`
    - Replace `updateFixedExpense` → `updateLancamentoFixo(id, {...})`
    - Replace `removeFixedExpense` (soft delete via `endMonth`) → `deleteLancamentoFixo(id)` (server soft deletes via excluidoEm)
  - Update Revenues section:
    - Replace `addRevenue` → `createLancamentoFixo({tipo: 'RECEITA', ...})`
    - Replace `updateRevenue` → `updateLancamentoFixo(id, {...})`
    - Replace `removeRevenue` (soft delete via `endMonth`) → `deleteLancamentoFixo(id)`
  - Replace payment tracking:
    - `upsertMonthOverride({type: 'fixedExpensePayment', paid: true})` → `updateLancamento(orcamentoId, lancamentoId, {statusDespesa: 'PAGO'})`
    - `clearMonthOverride({type: 'fixedExpensePayment'})` → `updateLancamento(orcamentoId, lancamentoId, {statusDespesa: 'ABERTO'})`
  - Update FinanceApp main component to use `FinanceApiContext` providers instead of old `FinanceContext`

  **Must NOT do**:
  - Do NOT remove old FinanceContext yet (T14)
  - Do NOT remove buildMonthView yet (T14)
  - Do NOT touch MonthOverride types yet (T14)

  **Recommended Agent Profile**:
  - **Category**: `deep`
    - Reason: Core UI migration — the most complex section because it replaces the central month derivation logic and maps two frontend concepts (FixedExpense + Revenue) into one server entity (LancamentoFixo)
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 4 (with T10, T12, T13)
  - **Blocks**: T14 (cleanup)
  - **Blocked By**: T9 (FinanceApiContext)

  **References** (CRITICAL):

  **Pattern References**:
  - `web/src/features/finance/selectors/buildMonth.ts:66-180` — THE function being replaced. Study exactly how it filters/expenses/revenues/installments and applies overrides.
  - `web/src/features/finance/components/sections/FixedExpenses.tsx` — Current despesas section component
  - `web/src/features/finance/components/MonthNav.tsx` — Current month navigation component
  - `web/src/features/finance/hooks/useFinanceActions.ts` — Current action hooks that need API equivalents
  - `web/src/features/finance/hooks/useMonthOverridesActions.ts` — Override actions being replaced by LancamentoMensal edits

  **API/Type References**:
  - `web/src/features/finance/lib/api/orcamentosMensaisService.ts` — (from T7) OrcamentoMensal API
  - `web/src/features/finance/lib/api/lancamentosMensaisService.ts` — (from T7) LancamentoMensal API (replaces overrides)
  - `web/src/features/finance/lib/api/lancamentosFixosService.ts` — (from T5) LancamentoFixo API (replaces FixedExpense + Revenue)

  **WHY Each Reference Matters**:
  - buildMonth.ts is the CENTER of the current app — understanding its exact logic is critical to replicate it with API data
  - MonthNav controls navigation — must handle the fetch-or-create pattern correctly
  - FixedExpenses and Revenue sections are the PRIMARY UI — must map LancamentoFixo(tipo=DESPESA/RECEITA) correctly
  - Override actions are being completely replaced — statusDespesa replaces paid boolean

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Month navigation shows correct budget
    Tool: Playwright
    Preconditions: Server running with test data for 2026-05
    Steps:
      1. Navigate to the app
      2. Click next month arrow to navigate to 2026-06
      3. Assert month header shows "Junho 2026" (or equivalent)
      4. Assert lancamentos from auto-import are displayed
      5. Click previous month to go back to 2026-05
      6. Assert month header shows "Maio 2026"
    Expected Result: Month navigation correctly fetches/creates budgets and displays data
    Failure Indicators: Blank screen, 404 errors, stale data
    Evidence: .sisyphus/evidence/task-11-month-navigation.png

  Scenario: Create despesa fixa via API
    Tool: Playwright
    Preconditions: Server running, current month has an orcamento
    Steps:
      1. Open add despesa fixa form
      2. Fill: description "Aluguel Test", value "1500", dueDay "5", category, paymentMethod
      3. Save
      4. Assert "Aluguel Test" appears in despesas list
      5. Assert network shows POST /api/orcamentos-mensais/lancamentos-fixos with tipo=DESPESA
    Expected Result: Despesa created via API and displayed
    Failure Indicators: API error, item not appearing, wrong tipo
    Evidence: .sisyphus/evidence/task-11-add-despesa.png

  Scenario: Mark despesa as PAGO
    Tool: Playwright
    Preconditions: Server running, despesa exists in current month
    Steps:
      1. Click payment checkbox on a despesa
      2. Assert checkbox is checked
      3. Assert network shows PUT /api/orcamentos-mensais/{id}/lancamentos/{id} with statusDespesa=PAGO
    Expected Result: Despesa marked as paid via statusDespesa
    Failure Indicators: MonthOverride created instead, API error, checkbox not persisting
    Evidence: .sisyphus/evidence/task-11-mark-paid.png
  ```

  **Commit**: YES (groups with T11)
  - Message: `feat(web): migrate despesas/receitas and month navigation to API`
  - Files: Updated components in `web/src/features/finance/components/`, `web/src/features/finance/FinanceApp.tsx`

- [ ] 12. Web: Migrate parcelamentos and faturas UI to API

  **What to do**:
  - Replace the Installments section UI with Parcelamento API calls:
    - `addInstallment` → `createParcelamento(cartaoId, {nome, valor, parcelas, mesInicio})`
    - `updateInstallment` → `updateParcelamento(cartaoId, id, {...})`
    - `removeInstallment` → `deleteParcelamento(cartaoId, id)` (soft delete)
  - Replace card bill overrides with Fatura API calls:
    - Creating a card bill → `createFatura(cartaoId, {orcamentoId, valor, mes, descricao})`
    - Updating a card bill → `updateFatura(cartaoId, id, {...})`
    - Deleting a card bill → `deleteFatura(cartaoId, id)`
  - Update component to show parcelamentos grouped by cartao
  - Display current installment progress (calculated from mesInicio + current month vs total parcelas)

  **Must NOT do**:
  - Do NOT remove old Installment type yet (T14)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: UI migration for parcelamentos and faturas sections
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 4 (with T10, T11, T13)
  - **Blocks**: T14 (cleanup)
  - **Blocked By**: T9 (FinanceApiContext)

  **References**:

  **Pattern References**:
  - `web/src/features/finance/components/sections/Installments.tsx` — Current installments section
  - `web/src/features/finance/components/summary/` — Summary dashboard that shows card bills
  - `web/src/features/finance/hooks/useCardDeleteReasons.ts` — Current card interaction hooks
  - `web/src/features/finance/hooks/useActiveFixedExpenses.ts` — Filters active expenses
  - `web/src/features/finance/hooks/useMonthPaymentMap.ts` — Current payment tracking (to be replaced)

  **API/Type References**:
  - `web/src/features/finance/lib/api/parcelamentosService.ts` — (from T6) Parcelamento API
  - `web/src/features/finance/lib/api/faturasService.ts` — (from T8) Fatura API
  - `web/src/features/finance/lib/api/cartoesService.ts` — (from T4) Cartoes API (parcelamentos nested under cartao)
  - `server/src/main/kotlin/orcamento/mensal/domain/cartoes/Parcelamento.kt` — Domain model (mesInicio, valor per installment, parcelas total)

  **WHY Each Reference Matters**:
  - Installments.tsx is the current UI for parcelamentos — must map to API-powered version
  - Summary hooks show how card bills are currently computed (via overrides) — Fatura replaces this
  - useMonthPaymentMap tracks payment status via overrides — replaced by LancamentoMensal.statusDespesa

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Create parcelamento via API
    Tool: Playwright
    Preconditions: Server running, at least one cartao exists
    Steps:
      1. Navigate to parcelamentos section
      2. Click add parcelamento
      3. Fill: name "Notebook", valor "500", parcelas "10", mesInicio current month, select a card
      4. Save
      5. Assert "Notebook" appears in parcelamentos list with installment details
      6. Assert network shows POST to /cartoes/{id}/parcelamentos
    Expected Result: Parcelamento created via API and displayed grouped by card
    Failure Indicators: API error, item not appearing, wrong card association
    Evidence: .sisyphus/evidence/task-12-add-parcelamento.png

  Scenario: Card bill (fatura) creation works
    Tool: Playwright
    Preconditions: Server running, cartao and orcamento exist
    Steps:
      1. Navigate to card bills section
      2. Create a new fatura for a card
      3. Fill: valor and mes
      4. Save
      5. Assert fatura appears in card bill summary
    Expected Result: Fatura created and linked to orcamento
    Evidence: .sisyphus/evidence/task-12-add-fatura.png
  ```

  **Commit**: YES (groups with T12)
  - Message: `feat(web): migrate parcelamentos and faturas UI to API`
  - Files: Updated components in `web/src/features/finance/components/sections/`, related hooks

- [ ] 13. Web: Migrate summary and charts to API data model

  **What to do**:
  - Update `summarySelectors.ts` to work with server response types instead of frontend derived types
  - Update chart data computation in `chartSeries.ts` to use LancamentoMensal types (tipo DESPESA/RECEITA, statusDespesa ABERTO/RESERVADO/PAGO) instead of the old override-based payment tracking
  - Update `SummaryDashboard` component to show totals computed from LancamentoMensal list instead of buildMonthView
  - Totals calculation:
    - **Despesas Fixas**: Sum LancamentoMensal where tipo=DESPESA from LancamentoFixo imports
    - **Receitas**: Sum LancamentoMensal where tipo=RECEITA
    - **Parcelamentos**: Sum parcelamento values for current month
    - **Saldo**: Receitas - Despesas
    - **Pago tracking**: Count LancamentoMensal where statusDespesa=PAGO
  - Ensure Chart.js visualizations render correctly with new data format

  **Must NOT do**:
  - Do NOT remove buildMonthView yet (T14)
  - Do NOT remove summarySelectors.ts old functions yet (T14)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Summary and chart data transformation — requires mapping between old derived format and new API format
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 4 (with T10, T11, T12)
  - **Blocks**: T14 (cleanup)
  - **Blocked By**: T9 (FinanceApiContext)

  **References**:

  **Pattern References**:
  - `web/src/features/finance/selectors/summarySelectors.ts` — Current summary computation (despesas, receitas, saldo, payment tracking)
  - `web/src/features/finance/lib/chartSeries.ts` — Current chart data builder
  - `web/src/features/finance/hooks/useCharts.ts` — Current chart hook
  - `web/src/features/finance/components/summary/SummaryDashboard.tsx` — Summary UI component

  **API/Type References**:
  - `web/src/features/finance/lib/api/types.ts` — (from T2) LancamentoMensalResponse with tipo, statusDespesa
  - `web/src/features/finance/lib/api/orcamentosMensaisService.ts` — (from T7) For fetching orcamento data including totals

  **WHY Each Reference Matters**:
  - summarySelectors.ts shows the EXACT calculation logic for totals — must be replicated with LancamentoMensal data
  - chartSeries.ts shows chart data format — must adapt to new types
  - The key mapping is: MonthOverride paid tracking → LancamentoMensal.statusDespesa (PAGO)

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Summary shows correct totals from API data
    Tool: Playwright
    Preconditions: Server running, orcamento with lancamentos exists
    Steps:
      1. Navigate to the app's summary/resumo tab
      2. Assert despesas fixas total matches sum of DESPESA lancamentos
      3. Assert receitas total matches sum of RECEITA lancamentos
      4. Assert saldo = receitas - despesas
    Expected Result: Summary dashboard shows correct financial totals
    Failure Indicators: NaN values, zero totals, mismatched amounts
    Evidence: .sisyphus/evidence/task-13-summary-totals.png

  Scenario: Charts render with API data
    Tool: Playwright
    Preconditions: Server running, data with multiple categories and cards
    Steps:
      1. Navigate to chart tab
      2. Assert pie/bar charts render without errors
      3. Assert chart labels match categoria names from API
    Expected Result: Charts render correctly with API-provided data
    Failure Indicators: Blank charts, error boundary, wrong labels
    Evidence: .sisyphus/evidence/task-13-charts-render.png
  ```

  **Commit**: YES (groups with T13)
  - Message: `feat(web): migrate summary and charts to API data model`
  - Files: `web/src/features/finance/selectors/summarySelectors.ts`, `web/src/features/finance/lib/chartSeries.ts`, `web/src/features/finance/components/summary/`

- [ ] 14. Web: Remove old code (IndexedDB, Dexie, MonthOverride, buildMonthView)

  **What to do**:
  - Remove the following files entirely:
    - `web/src/features/finance/lib/storage.ts` — Dexie/IndexedDB storage
    - `web/src/features/finance/lib/schema.ts` — Old schema definitions (replace with API-type imports)
    - `web/src/features/finance/lib/ids.ts` — Old ID generation (no longer needed, server generates IDs)
    - `web/src/features/finance/lib/migrations.ts` — Old IndexedDB migration logic
    - `web/src/features/finance/lib/normalizers.ts` — Old data normalization (API responses are already normalized)
    - `web/src/features/finance/selectors/buildMonth.ts` — Month derivation logic (replaced by API)
    - `web/src/features/finance/selectors/monthOverrideSelectors.ts` — Override selectors (replaced by statusDespesa)
    - `web/src/features/finance/domain/constants.ts` — Old OVERRIDE_TYPES and ALLOWED_PAYMENT_METHODS (replaced by server enums)
  - Remove old types from `web/src/features/finance/domain/types.ts`:
    - `FixedExpense`, `Installment`, `Revenue`, `MonthOverride`, `OverrideType`, `MonthView*` types
    - `FinanceState` (replaced by FinanceApiContext state)
    - `Settings.cardBills` field (now API-driven)
    - `Meta` type (no longer needed)
  - Remove old state management:
    - `web/src/features/finance/context/FinanceContext.tsx` — Old context (replaced by FinanceApiContext)
    - `web/src/features/finance/context/financeContextInternals.ts` — Old hydration/persistence
    - `web/src/features/finance/domain/stateReducers.ts` — Old reducers (replaced by API calls)
    - `web/src/features/finance/domain/actionFactory.ts` — Old action factory (replaced by API calls)
  - Remove old hooks that depend on removed code:
    - `web/src/features/finance/hooks/useMonthOverridesActions.ts`
    - `web/src/features/finance/hooks/useActiveFixedExpenses.ts` (replaced by API-driven version)
    - `web/src/features/finance/hooks/useMonthPaymentMap.ts` (replaced by statusDespesa)
  - Update `web/src/features/finance/lib/financeRepository.ts` — Remove IFinanceRepository interface
  - Remove Dexie dependency from `web/package.json`
  - Remove old test files for removed modules
  - Verify `npm run build` compiles with zero errors after cleanup
  - Verify `npm test` passes (with remaining tests)

  **Must NOT do**:
  - Do NOT remove any API service files (lib/api/*)
  - Do NOT remove FinanceApiContext or its hooks
  - Do NOT remove chart libraries or UI framework code
  - Do NOT break the build — if removing a file causes import errors, update the importers too

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Mostly deletion work with careful dependency cleanup. Requires precision but not deep domain reasoning.
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 5 (after Wave 4)
  - **Blocks**: T15 (tests/docs), F1-F4 (reviews)
  - **Blocked By**: T10, T11, T12, T13 (all UI tasks must be done)

  **References** (CRITICAL):

  **Pattern References**:
  - `web/src/features/finance/lib/storage.ts` — File to DELETE (Dexie storage)
  - `web/src/features/finance/domain/types.ts` — File to EDIT (remove old types, keep new API types)
  - `web/src/features/finance/context/FinanceContext.tsx` — File to DELETE (old context)
  - `web/src/features/finance/selectors/buildMonth.ts` — File to DELETE (month derivation)
  - `web/package.json` — File to EDIT (remove Dexie dependency)

  **WHY Each Reference Matters**:
  - These are the EXACT files to delete or edit — listing them ensures nothing is missed
  - types.ts is the trickiest because it has both old types to remove and potentially some shared utility types to keep

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Build compiles after removing old code
    Tool: Bash
    Steps:
      1. `cd web && npm run build`
      2. Assert zero build errors
    Expected Result: Clean build with no errors
    Failure Indicators: Type errors, missing imports, unresolved references
    Evidence: .sisyphus/evidence/task-14-build-success.txt

  Scenario: No references to removed modules remain
    Tool: Bash
    Steps:
      1. `cd web && grep -r "buildMonthView\|MonthOverride\|IFinanceRepository\|Dexie\|storage\.ts" src/ --include="*.ts" --include="*.tsx"`
      2. Assert zero matches (excluding API service files that legitimately reference types)
    Expected Result: No remaining imports of removed modules
    Failure Indicators: Orphaned imports, dead references
    Evidence: .sisyphus/evidence/task-14-no-remaining-refs.txt

  Scenario: Dexie removed from dependencies
    Tool: Bash
    Steps:
      1. `cd web && grep "dexie" package.json`
      2. Assert zero matches in dependencies or devDependencies
    Expected Result: Dexie dependency removed
    Failure Indicators: Dexie still listed in package.json
    Evidence: .sisyphus/evidence/task-14-dexie-removed.txt
  ```

  **Commit**: YES (groups with T14)
  - Message: `refactor(web): remove IndexedDB, Dexie, MonthOverride, and buildMonthView`
  - Files: Multiple deletions and imports cleanup across `web/src/features/finance/`
  - Pre-commit: `cd web && npm run build`

- [ ] 15. Web: Rewrite tests and update ARCHITECTURE.md

  **What to do**:
  - Identify all test files that reference removed modules (buildMonth, MonthOverride, Dexie, stateReducers, etc.)
  - Delete tests for removed functionality:
    - Tests for buildMonthView derivation
    - Tests for MonthOverride system (upsert, clear, apply)
    - Tests for Dexie storage persistence
    - Tests for old state reducers
  - Write new tests for:
    - API service functions (categoriasService, cartoesService, lancamentosFixosService, etc.)
    - FinanceApiContext state management
    - Mapper functions (FixedExpense → LancamentoFixo DESPESA, Revenue → LancamentoFixo RECEITA)
    - Month navigation (fetch-or-create flow)
  - Update `web/ARCHITECTURE.md` to reflect new architecture:
    - Replace "IndexedDB via Dexie" with "API via Ktor backend"
    - Replace "Context + hooks + Dexie" state management with "FinanceApiContext + API calls"
    - Replace "Month is derived, not stored" with "Month view from OrcamentoMensal + LancamentoMensal API"
    - Remove MonthOverride section entirely
    - Update data model section to reflect server entities
    - Update "Como o Mês é Construído" section — month navigation fetches orcamento, lancamentos come from API
    - Remove "Regras de Edição" section about global vs month overrides (replaced by direct LancamentoMensal edits)
    - Add section about API client and service layer
  - Run `npm test` and ensure all tests pass

  **Must NOT do**:
  - Do NOT write tests for functionality that doesn't exist yet
  - Do NOT modify server code or documentation
  - Do NOT add tests that require server to be running (unit tests only)

  **Recommended Agent Profile**:
  - **Category**: `writing`
    - Reason: Test writing and documentation update task
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: NO (after T14)
  - **Parallel Group**: Wave 5 (with T14)
  - **Blocks**: F1-F4 (reviews)
  - **Blocked By**: T14 (all old code removed)

  **References**:

  **Pattern References**:
  - `web/src/features/finance/tests/` — All existing test files to review and update
  - `web/ARCHITECTURE.md` — Current architecture documentation to update
  - `web/src/features/finance/lib/api/` — New API services to test

  **Test References**:
  - `web/vitest.config.ts` — Test framework configuration
  - Existing test patterns in `web/src/features/finance/tests/` — Follow existing test style

  **WHY Each Reference Matters**:
  - Must identify every test that references removed modules to avoid build failures
  - ARCHITECTURE.md is the source of truth for developers and AI agents — must accurately reflect new architecture
  - New API services need units tests for mapper functions and service call signatures

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: All tests pass after rewrite
    Tool: Bash
    Steps:
      1. `cd web && npm test`
      2. Assert all tests pass (zero failures)
    Expected Result: Full test suite green
    Failure Indicators: Test failures, type errors in test files
    Evidence: .sisyphus/evidence/task-15-tests-pass.txt

  Scenario: No tests reference removed modules
    Tool: Bash
    Steps:
      1. `cd web && grep -r "buildMonthView\|MonthOverride\|Dexie\|IFinanceRepository" src/ --include="*.test.*"`
      2. Assert zero matches
    Expected Result: No test references to removed functionality
    Failure Indicators: Tests importing removed modules
    Evidence: .sisyphus/evidence/task-15-no-old-test-refs.txt

  Scenario: ARCHITECTURE.md reflects new architecture
    Tool: Bash
    Steps:
      1. Read ARCHITECTURE.md
      2. Verify "API via Ktor" appears (not "IndexedDB via Dexie")
      3. Verify "FinanceApiContext" appears as state management pattern
      4. Verify "OrcamentoMensal" and "LancamentoMensal" appear as data model
      5. Verify "MonthOverride" does NOT appear
      6. Verify "buildMonthView" does NOT appear (or appears only as "removed")
    Expected Result: Documentation accurately reflects API-based architecture
    Evidence: .sisyphus/evidence/task-15-architecture.md
  ```

  **Commit**: YES (groups with T15)
  - Message: `test(web): rewrite tests for API-based architecture; docs: update ARCHITECTURE.md`
  - Files: `web/src/features/finance/tests/`, `web/ARCHITECTURE.md`
  - Pre-commit: `cd web && npm test && npm run build`

- [ ] 2. Web: Create API client infrastructure with TypeScript types and mappers

  **What to do**:
  - Create `web/src/features/finance/lib/api/` directory with:
    - `client.ts` — Fetch wrapper with base URL from env (`http://localhost:8080`), error handling, and response parsing
    - `types.ts` — TypeScript interfaces matching ALL server DTOs (OrcamentoMensalRequest/Response, LancamentoMensalRequest/Response, LancamentoFixoRequest/Response, CategoriaRequest/Response, CartaoRequest/Response, ParcelamentoRequest/Response, FaturaRequest/Response)
    - `mappers.ts` — Functions to convert between server DTOs and frontend domain types (and vice versa), handling field name differences (e.g., `descricao` ↔ `name`, `valor` ↔ `amount`, Long IDs ↔ string IDs)
    - `config.ts` — API base URL configuration with environment variable support
  - Type mapper considerations:
    - `FormaPagamento` enum: BOLETO/PIX/CARTAO ↔ frontend's `boleto`/`pix`/`cartao`
    - `TipoLancamento` enum: RECEITA/DESPESA ↔ frontend concept
    - `StatusDespesa` enum: ABERTO/RESERVADO/PAGO ↔ will replace `paid:boolean`
    - `AnoMes` format: `YYYY-MM` on input, server stores `ano` + `mes` separately
    - IDs: Server uses Long, frontend currently uses prefixed strings. New code should use Long/number.
  - All types should use `BigDecimalJson` equivalent as `string` in TypeScript (monetary values as strings to avoid floating point)
  - Configure Vite dev proxy for `/api/*` routes to `http://localhost:8080`

  **Must NOT do**:
  - Do NOT connect any API calls to FinanceContext yet (that's T9)
  - Do NOT remove or modify any existing code (IFinanceRepository, Dexie, etc.)
  - Do NOT create React hooks yet — just the service layer and types

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Foundation task creating static types and utility functions, no UI or state management
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with T1, T3)
  - **Blocks**: T4, T5, T6, T7, T8 (all API services)
  - **Blocked By**: None (can start immediately)

  **References** (CRITICAL):

  **Pattern References**:
  - `web/src/features/finance/lib/financeRepository.ts:1-7` — IFinanceRepository interface pattern for data access
  - `web/src/features/finance/lib/storage.ts` — Current Dexie-based repository, understand the interface contract
  - `web/src/features/finance/domain/types.ts` — Current frontend types to map FROM/TO

  **API/Type References**:
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/OrcamentosMensais.kt` — OrcamentoMensal DTOs (Request/Response)
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/LancamentosFixos.kt` — LancamentoFixo DTOs
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/Categorias.kt` — Categoria DTOs
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/Cartoes.kt` — Cartao DTOs
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/Parcelamentos.kt` — Parcelamento DTOs
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/Faturas.kt` — Fatura DTOs
  - `server/src/main/kotlin/orcamento/mensal/domain/lancamentos/TipoLancamento.kt` — RECEITA/DESPESA enum
  - `server/src/main/kotlin/orcamento/mensal/domain/lancamentos/LancamentoMensal.kt` — StatusDespesa enum (ABERTO/RESERVADO/PAGO)
  - `server/src/main/kotlin/orcamento/mensal/domain/lancamentos/LancamentoFixo.kt` — FormaPagamento enum (BOLETO/PIX/CARTAO)

  **External References**:
  - Vite proxy configuration: `https://vitejs.dev/config/server-options.html#server-proxy`

  **WHY Each Reference Matters**:
  - financeRepository.ts defines the current data access pattern that API services must replace
  - storage.ts shows the current Dexie schema — understanding the data shape helps create accurate mappers
  - types.ts has ALL current frontend types that need mapping to/from server DTOs
  - Server DTOs define the exact shape the API expects/returns — types.ts must match these exactly
  - Enums (TipoLancamento, StatusDespesa, FormaPagamento) define enum values that need TypeScript equivalents

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: API client can make a basic GET request
    Tool: Bash
    Preconditions: web project has dependencies installed
    Steps:
      1. `cd web && npx tsc --noEmit src/features/finance/lib/api/client.ts`
      2. Assert zero type errors
      3. Verify client.ts exports a function that takes a URL path and returns a Promise
    Expected Result: TypeScript compiles without errors
    Failure Indicators: Type errors, missing imports, build fail
    Evidence: .sisyphus/evidence/task-2-api-client-compiles.txt

  Scenario: TypeScript types match server DTOs
    Tool: Bash
    Preconditions: web project has dependencies installed
    Steps:
      1. `cd web && npx tsc --noEmit src/features/finance/lib/api/types.ts`
      2. Assert zero type errors
      3. Verify types.ts includes interfaces for all 6 entity groups (Categorias, Cartoes, LancamentosFixos, Parcelamentos, OrcamentosMensais, LancamentosMensais, Faturas)
    Expected Result: TypeScript compiles, all DTO types present
    Failure Indicators: Missing entity types, type errors
    Evidence: .sisyphus/evidence/task-2-api-types-check.txt

  Scenario: Mappers convert between frontend and server types
    Tool: Bash
    Preconditions: web project has dependencies installed
    Steps:
      1. `cd web && npx tsc --noEmit src/features/finance/lib/api/mappers.ts`
      2. Assert zero type errors
      3. Verify mappers exist for all 6 entity groups
    Expected Result: TypeScript compiles, mappers exist for bidirectional conversion
    Failure Indicators: Missing mappers, type mismatches
    Evidence: .sisyphus/evidence/task-2-api-mappers-check.txt
  ```

  **Commit**: YES (groups with T2)
  - Message: `feat(web): add API client infrastructure with types and mappers`
  - Files: `web/src/features/finance/lib/api/client.ts`, `web/src/features/finance/lib/api/types.ts`, `web/src/features/finance/lib/api/mappers.ts`, `web/src/features/finance/lib/api/config.ts`, `web/vite.config.ts`
  - Pre-commit: `cd web && npm run build`

- [ ] 3. Server: Update MEMORY.md with accurate frontend integration info

  **What to do**:
  - Update `server/MEMORY.md` to:
    - Replace the outdated "Frontend (web-app)" section with current frontend stack (React 18 + Vite + Chart.js + Dexie)
    - Remove the duplicate "Frontend (web-app)" section
    - Add a "Frontend Integration" section documenting:
      - API base URL: `http://localhost:8080`
      - All 6 entity endpoints with their CRUD operations
      - `anoMes` query parameter on orcamentos-mensais
      - Data model mapping (FixedExpense → LancamentoFixo tipo DESPESA, Revenue → LancamentoFixo tipo RECEITA, etc.)
      - Key enum mappings (FormaPagamento, TipoLancamento, StatusDespesa)
      - Note about `idUsuario=1` being hardcoded until Firebase auth is enabled

  **Must NOT do**:
  - Do NOT modify any Kotlin source code
  - Do NOT add API endpoints (that's T1)
  - Do NOT remove technical details that are still valid

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Documentation-only task, no code changes
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with T1, T2)
  - **Blocks**: None
  - **Blocked By**: None

  **References**:

  **Pattern References**:
  - `server/MEMORY.md` — Current content to update
  - `server/AGENTS.md` — Existing agent documentation format

  **WHY Each Reference Matters**:
  - MEMORY.md is the source of truth for project context — must be accurate for future agents

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: MEMORY.md is updated and accurate
    Tool: Bash
    Preconditions: None
    Steps:
      1. Read server/MEMORY.md
      2. Verify it contains "Frontend Integration" section
      3. Verify it lists all 6 entity groups
      4. Verify duplicate "Frontend (web-app)" sections are removed
      5. Verify `idUsuario=1` hardcoded note is present
    Expected Result: MEMORY.md is complete and accurate
    Failure Indicators: Missing sections, outdated info, duplicates remaining
    Evidence: .sisyphus/evidence/task-3-memory-md-updated.txt
  ```

  **Commit**: YES (groups with T3)
  - Message: `docs(api): update MEMORY.md with frontend integration info`
  - Files: `server/MEMORY.md`

- Pre-commit: `cd server && ./gradlew build`

- [ ] 4. Web: Categorias + Cartões API services

  **What to do**:
  - Create `web/src/features/finance/lib/api/categoriasService.ts`:
    - `fetchCategorias()` → `GET /api/orcamentos-mensais/categorias`
    - `createCategoria(data)` → `POST /api/orcamentos-mensais/categorias`
    - `setupCategoriasPadrao()` → `POST /api/orcamentos-mensais/categorias/setup`
    - `updateCategoria(id, data)` → `PUT /api/orcamentos-mensais/categorias/{id}`
    - `deleteCategoria(id)` → `DELETE /api/orcamentos-mensais/categorias/{id}`
  - Create `web/src/features/finance/lib/api/cartoesService.ts`:
    - `fetchCartoes()` → `GET /api/orcamentos-mensais/cartoes`
    - `fetchCartao(id)` → `GET /api/orcamentos-mensais/cartoes/{id}`
    - `createCartao(data)` → `POST /api/orcamentos-mensais/cartoes`
    - `updateCartao(id, data)` → `PUT /api/orcamentos-mensais/cartoes/{id}`
    - `deleteCartao(id)` → `DELETE /api/orcamentos-mensais/cartoes/{id}`
  - All service functions should use the API client from T2 and return typed responses
  - Use mapper functions from T2 to convert between server DTOs and frontend types where needed

  **Must NOT do**:
  - Do NOT connect these services to FinanceContext yet (that's T9)
  - Do NOT modify any UI components (that's T10)
  - Do NOT remove existing hardcoded categories or card bills yet

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Two straightforward CRUD service modules with well-defined API contracts
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T5, T6, T7, T8)
  - **Blocks**: T10 (Category + Card settings UI)
  - **Blocked By**: T2 (API client + types)

  **References**:

  **Pattern References**:
  - `web/src/features/finance/lib/api/client.ts` — API client from T2
  - `web/src/features/finance/lib/api/types.ts` — Types from T2
  - `web/src/features/finance/lib/api/mappers.ts` — Mappers from T2

  **API/Type References**:
  - `server/src/main/kotlin/orcamento/mensal/routes/CategoriasRoutes.kt:15-99` — Categoria endpoint definitions
  - `server/src/main/kotlin/orcamento/mensal/routes/CartoesRoutes.kt:17-111` — Cartao endpoint definitions (includes nested parcelamentos and faturas routes)
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/Categorias.kt` — Categoria DTOs (nome, icone)
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/Cartoes.kt` — Cartao DTOs (nome, icone, cor)

  **WHY Each Reference Matters**:
  - Routes define exact HTTP methods, paths, and response codes
  - DTOs define exact request/response shapes the services must match
  - CartoesRoutes shows nested routes for parcelamentos and faturas (important for T6/T8)

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Categorias service functions compile correctly
    Tool: Bash
    Steps:
      1. `cd web && npx tsc --noEmit`
      2. Assert zero type errors referencing categoriasService
    Expected Result: Clean compilation
    Evidence: .sisyphus/evidence/task-4-categorias-service-compiles.txt

  Scenario: Cartoes service functions compile correctly
    Tool: Bash
    Steps:
      1. `cd web && npx tsc --noEmit`
      2. Assert zero type errors referencing cartoesService
    Expected Result: Clean compilation
    Evidence: .sisyphus/evidence/task-4-cartoes-service-compiles.txt

  Scenario: Services export all CRUD operations
    Tool: Bash
    Preconditions: web project compiles
    Steps:
      1. Grep categoriasService.ts for fetchCategorias, createCategoria, setupCategoriasPadrao, updateCategoria, deleteCategoria — all 5 must be exported
      2. Grep cartoesService.ts for fetchCartoes, fetchCartao, createCartao, updateCartao, deleteCartao — all 5 must be exported
    Expected Result: All 10 functions exported across both files
    Evidence: .sisyphus/evidence/task-4-services-complete.txt
  ```

  **Commit**: YES (groups with T4)
  - Message: `feat(web): add categorias and cartões API services`
  - Files: `web/src/features/finance/lib/api/categoriasService.ts`, `web/src/features/finance/lib/api/cartoesService.ts`

- [ ] 5. Web: Lançamentos Fixos API service

  **What to do**:
  - Create `web/src/features/finance/lib/api/lancamentosFixosService.ts`:
    - `fetchLancamentosFixos()` → `GET /api/orcamentos-mensais/lancamentos-fixos`
    - `fetchLancamentoFixo(id)` → `GET /api/orcamentos-mensais/lancamentos-fixos/{id}`
    - `createLancamentoFixo(data)` → `POST /api/orcamentos-mensais/lancamentos-fixos`
      - Must include: `tipo` ("RECEITA" or "DESPESA"), `descricao`, `valor`, `diaVencimento`, `mesInicio`, `formaPagamento`, `idCategoria`, optional `idCartao`
    - `updateLancamentoFixo(id, data)` → `PUT /api/orcamentos-mensais/lancamentos-fixos/{id}`
    - `deleteLancamentoFixo(id)` → `DELETE /api/orcamentos-mensais/lancamentos-fixos/{id}`
  - This service replaces BOTH FixedExpense and Revenue from the frontend
  - The `tipo` field determines if it's a DESPESA (was FixedExpense) or RECEITA (was Revenue)
  - Soft delete means `DELETE` sets `excluidoEm` — the item becomes inactive, not removed
  - Use mapper functions to convert between frontend's FixedExpense/Revenue and server's LancamentoFixo

  **Must NOT do**:
  - Do NOT modify FinanceContext or UI components yet
  - Do NOT remove FixedExpense or Revenue types from types.ts yet

  **Recommended Agent Profile**:
  - **Category**: `deep`
    - Reason: This is the most complex CRUD because it unifies two frontend concepts (FixedExpense + Revenue) into one server entity (LancamentoFixo with tipo). The mapper is non-trivial.
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T4, T6, T7, T8)
  - **Blocks**: T11 (Despesas/Receitas UI)
  - **Blocked By**: T2 (API client + types)

  **References**:

  **Pattern References**:
  - `web/src/features/finance/lib/api/client.ts` — API client from T2
  - `web/src/features/finance/domain/types.ts:1-37` — FixedExpense and Revenue interfaces to map FROM
  - `web/src/features/finance/domain/stateReducers.ts:31-105` — addFixedExpense, addRevenue, updateFixedExpense, updateRevenue, removeFixedExpense, removeRevenue — understand the current mutation patterns

  **API/Type References**:
  - `server/src/main/kotlin/orcamento/mensal/routes/LancamentosFixosRoutes.kt:15-105` — Endpoint definitions
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/LancamentosFixos.kt:1-65` — DTO shapes: LancamentoFixoRequest, LancamentoFixoUpdateRequest, LancamentoFixoResponse
  - `server/src/main/kotlin/orcamento/mensal/domain/lancamentos/LancamentoFixo.kt` — Domain model with FormaPagamento enum (BOLETO, PIX, CARTAO) and tipo (RECEITA, DESPESA)

  **WHY Each Reference Matters**:
  - stateReducers.ts shows the exact mutation patterns the UI currently uses — the new service must provide equivalent operations
  - LancamentoFixo DTOs show the exact request/response shapes — types must match exactly
  - The tipo/RECEITA/DESPESA mapping is the key complexity — Revenue → tipo=RECEITA, FixedExpense → tipo=DESPESA
  - FormaPagamento enum maps: BOLETO ↔ 'boleto', PIX ↔ 'pix', CARTAO ↔ 'cartao'

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: LancamentosFixos service compiles correctly
    Tool: Bash
    Steps:
      1. `cd web && npx tsc --noEmit`
      2. Assert zero type errors referencing lancamentosFixosService
    Expected Result: Clean compilation
    Evidence: .sisyphus/evidence/task-5-lancamentos-fixos-service-compiles.txt

  Scenario: Mapper correctly converts FixedExpense to LancamentoFixo DESPESA
    Tool: Bash
    Preconditions: web project compiles
    Steps:
      1. Check that mappers.ts contains mapper from FixedExpense → LancamentoFixoRequest with tipo=DESPESA
      2. Check that mapper converts: name→descricao, amount→valor, dueDay→diaVencimento, startMonth→mesInicio, category→idCategoria, paymentMethod→formaPagamento
    Expected Result: Mapper exists and correctly maps all FixedExpense fields to LancamentoFixo DESPESA
    Evidence: .sisyphus/evidence/task-5-fixedexpense-mapper.txt

  Scenario: Mapper correctly converts Revenue to LancamentoFixo RECEITA
    Tool: Bash
    Preconditions: web project compiles
    Steps:
      1. Check that mappers.ts contains mapper from Revenue → LancamentoFixoRequest with tipo=RECEITA
      2. Check that mapper converts: name→descricao, baseAmount→valor, startMonth→mesInicio
    Expected Result: Mapper exists and correctly maps Revenue fields to LancamentoFixo RECEITA
    Evidence: .sisyphus/evidence/task-5-revenue-mapper.txt
  ```

  **Commit**: YES (groups with T5)
  - Message: `feat(web): add lançamentos fixos API service`
  - Files: `web/src/features/finance/lib/api/lancamentosFixosService.ts`, `web/src/features/finance/lib/api/mappers.ts` (updated)

- [ ] 6. Web: Parcelamentos API service

  **What to do**:
  - Create `web/src/features/finance/lib/api/parcelamentosService.ts`:
    - `fetchParcelamentos(cartaoId)` → `GET /api/orcamentos-mensais/cartoes/{cartaoId}/parcelamentos`
    - `fetchParcelamento(cartaoId, id)` → `GET /api/orcamentos-mensais/cartoes/{cartaoId}/parcelamentos/{id}`
    - `createParcelamento(cartaoId, data)` → `POST /api/orcamentos-mensais/cartoes/{cartaoId}/parcelamentos`
      - Must include: `nome`, `valor` (per-installment), `parcelas`, `mesInicio`
    - `updateParcelamento(cartaoId, id, data)` → `PUT /api/orcamentos-mensais/cartoes/{cartaoId}/parcelamentos/{id}`
    - `deleteParcelamento(cartaoId, id)` → `DELETE /api/orcamentos-mensais/cartoes/{cartaoId}/parcelamentos/{id}`
  - Note: `valor` in Parcelamento = per-installment value (same as frontend's `installmentValue`), NOT total value
  - Note: Parcelamentos are NESTED under Cartao — all endpoints require `cartaoId`
  - Note: Soft delete via `excluidoEm` — deleted parcelamentos are not removed from DB

  **Must NOT do**:
  - Do NOT modify FinanceContext or UI components yet
  - Do NOT remove Installment type from types.ts yet

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Standard CRUD service with nested routes. Simplified by the fact that `valor` = per-installment.
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T4, T5, T7, T8)
  - **Blocks**: T12 (Parcelamentos + Faturas UI)
  - **Blocked By**: T2 (API client + types)

  **References**:

  **API/Type References**:
  - `server/src/main/kotlin/orcamento/mensal/routes/ParcelamentosRoutes.kt:14-110` — Nested route structure under `/{cartaoId}/parcelamentos`
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/Parcelamentos.kt:1-48` — Request/Response DTOs
  - `web/src/features/finance/domain/types.ts:15-26` — Installment interface to map FROM

  **WHY Each Reference Matters**:
  - Nested routes require cartaoId in every call — this is a key difference from flat endpoints
  - Installment mapper must handle currentInstallment auto-progress (frontend) vs static parcelas (server)

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Parcelamentos service compiles correctly
    Tool: Bash
    Steps:
      1. `cd web && npx tsc --noEmit`
      2. Assert zero type errors referencing parcelamentosService
    Expected Result: Clean compilation
    Evidence: .sisyphus/evidence/task-6-parcelamentos-service-compiles.txt

  Scenario: Service uses nested cartaoId routes
    Tool: Bash
    Steps:
      1. Grep parcelamentosService.ts for `{cartaoId}` URL pattern
      2. Verify all 5 functions include cartaoId in the URL path
    Expected Result: All functions use nested route pattern /cartoes/{cartaoId}/parcelamentos/...
    Evidence: .sisyphus/evidence/task-6-nested-routes.txt
  ```

  **Commit**: YES (groups with T6)
  - Message: `feat(web): add parcelamentos API service`
  - Files: `web/src/features/finance/lib/api/parcelamentosService.ts`

- [ ] 7. Web: Orçamento Mensal + Lançamentos Mensais API service

  **What to do**:
  - Create `web/src/features/finance/lib/api/orcamentosMensaisService.ts`:
    - `fetchOrcamentos()` → `GET /api/orcamentos-mensais`
    - `fetchOrcamentoByAnoMes(anoMes)` → `GET /api/orcamentos-mensais?anoMes=YYYY-MM` (uses T1 endpoint)
    - `fetchOrcamentoById(id)` → `GET /api/orcamentos-mensais/{id}`
    - `createOrcamento(anoMes)` → `POST /api/orcamentos-mensais` with `{idUsuario: 1, anoMes: "YYYY-MM"}`
      - NOTE: Creating an orcamento automatically imports LancamentoFixos (server behavior)
    - `deleteOrcamento(id)` → `DELETE /api/orcamentos-mensais/{id}`
  - Create `web/src/features/finance/lib/api/lancamentosMensaisService.ts`:
    - `fetchLancamentos(orcamentoId)` → `GET /api/orcamentos-mensais/{id}/lancamentos`
    - `createLancamento(orcamentoId, data)` → `POST /api/orcamentos-mensais/{id}/lancamentos`
      - Must include: `descricao`, `valor`, `tipo` ("RECEITA" or "DESPESA"), optional `statusDespesa`
    - `updateLancamento(orcamentoId, lancamentoId, data)` → `PUT /api/orcamentos-mensais/{id}/lancamentos/{lancamentoId}`
      - Key: This replaces the MonthOverride system. To change an amount for one month, edit the LancamentoMensal directly.
      - Key: To mark as paid, set `statusDespesa` to "PAGO"
    - `deleteLancamento(orcamentoId, lancamentoId)` → `DELETE /api/orcamentos-mensais/{id}/lancamentos/{lancamentoId}`
    - `importarLancamentosFixos(orcamentoId)` → `POST /api/orcamentos-mensais/{id}/lancamentos-fixos/importar`
  - These two services are combined in one task because they are tightly coupled (lancamentos live inside orcamentos)

  **Must NOT do**:
  - Do NOT modify FinanceContext or UI components yet
  - Do NOT remove MonthOverride system yet
  - Do NOT change month navigation behavior yet

  **Recommended Agent Profile**:
  - **Category**: `deep`
    - Reason: Two tightly coupled entities with complex interactions (auto-import, nested routes, status management). This is the CORE of the integration.
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES (within wave)
  - **Parallel Group**: Wave 2 (with T4, T5, T6, T8)
  - **Blocks**: T9 (FinanceContext refactor)
  - **Blocked By**: T1 (ano-mês endpoint), T2 (API client + types)

  **References**:

  **Pattern References**:
  - `web/src/features/finance/selectors/buildMonth.ts:66-180` — buildMonthView() — this is what OrcamentoMensal + LancamentoMensal will REPLACE
  - `web/src/features/finance/domain/stateReducers.ts:176-213` — upsertMonthOverride and clearMonthOverride — these are REPLACED by LancamentosMensais edits
  - `web/src/features/finance/hooks/useMonthOverridesActions.ts` — Current override actions hook that will be replaced

  **API/Type References**:
  - `server/src/main/kotlin/orcamento/mensal/routes/OrcamentosMensaisRoutes.kt:13-177` — Full route definitions including nested lancamentos
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/OrcamentosMensais.kt:1-86` — DTOs for OrcamentoMensal and LancamentoMensal
  - `server/src/main/kotlin/orcamento/mensal/service/OrcamentosMensaisService.kt:20-44` — Auto-import behavior on criar()
  - `server/src/main/kotlin/orcamento/mensal/service/lancamentos/LancamentosMensaisService.kt:25-63` — Import and CRUD logic
  - `server/src/main/kotlin/orcamento/mensal/domain/lancamentos/LancamentoMensal.kt:19-23` — StatusDespesa enum (ABERTO, RESERVADO, PAGO)

  **WHY Each Reference Matters**:
  - buildMonth.ts is the function being REPLACED — understanding its logic is critical for the new API-based approach
  - OrcamentosMensaisService.criar() auto-imports LancamentoFixos — this affects the UX flow (navigate to month → create budget → auto-imported)
  - StatusDespesa replaces the MonthOverride paid system — ABERTO/RESERVADO/PAGO instead of boolean
  - The `importarLancamentosFixos` endpoint allows re-importing if needed

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: OrcamentosMensais service compiles correctly
    Tool: Bash
    Steps:
      1. `cd web && npx tsc --noEmit`
      2. Assert zero type errors referencing orcamentosMensaisService
    Expected Result: Clean compilation
    Evidence: .sisyphus/evidence/task-7-orcamentos-service-compiles.txt

  Scenario: LancamentosMensais service compiles correctly
    Tool: Bash
    Steps:
      1. `cd web && npx tsc --noEmit`
      2. Assert zero type errors referencing lancamentosMensaisService
    Expected Result: Clean compilation
    Evidence: .sisyphus/evidence/task-7-lancamentos-service-compiles.txt

  Scenario: Services handle nested routes correctly
    Tool: Bash
    Steps:
      1. Grep lancamentosMensaisService.ts for `orcamentos-mensais/{id}/lancamentos` URL pattern
      2. Verify all 4 lancamento functions include orcamentoId in the URL
    Expected Result: All lancamento functions use nested route /orcamentos-mensais/{orcamentoId}/lancamentos/...
    Evidence: .sisyphus/evidence/task-7-nested-routes.txt
  ```

  **Commit**: YES (groups with T7)
  - Message: `feat(web): add orçamento mensal and lançamentos mensais API services`
  - Files: `web/src/features/finance/lib/api/orcamentosMensaisService.ts`, `web/src/features/finance/lib/api/lancamentosMensaisService.ts`

- [ ] 8. Web: Faturas API service

  **What to do**:
  - Create `web/src/features/finance/lib/api/faturasService.ts`:
    - `fetchFaturas(cartaoId)` → `GET /api/orcamentos-mensais/cartoes/{cartaoId}/faturas`
    - `fetchFatura(cartaoId, id)` → `GET /api/orcamentos-mensais/cartoes/{cartaoId}/faturas/{id}`
    - `createFatura(cartaoId, data)` → `POST /api/orcamentos-mensais/cartoes/{cartaoId}/faturas`
      - Must include: `orcamentoId`, `valor`, `mes`, `descricao`
    - `updateFatura(cartaoId, id, data)` → `PUT /api/orcamentos-mensais/cartoes/{cartaoId}/faturas/{id}`
    - `deleteFatura(cartaoId, id)` → `DELETE /api/orcamentos-mensais/cartoes/{cartaoId}/faturas/{id}`
  - Note: Faturas are nested under Cartao and also linked to a LancamentoMensal (via `idLancamento`)
  - Note: Faturas replace the frontend's current "card bill" override system

  **Must NOT do**:
  - Do NOT modify FinanceContext or UI components yet

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Standard CRUD with nested routes, similar pattern to Parcelamentos
  - **Skills**: `[]`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with T4, T5, T6, T7)
  - **Blocks**: T12 (Parcelamentos + Faturas UI)
  - **Blocked By**: T2 (API client + types)

  **References**:

  **API/Type References**:
  - `server/src/main/kotlin/orcamento/mensal/routes/FaturasRoutes.kt:15-107` — Nested route definitions
  - `server/src/main/kotlin/orcamento/mensal/api/dtos/Faturas.kt:1-42` — Fatura DTOs including `orcamentoId`, `valor`, `mes`, `descricao`
  - `server/src/main/kotlin/orcamento/mensal/domain/cartoes/Fatura.kt:1-17` — Domain model with `idCartao`, `idLancamento`, `valor`, `mes`

  **WHY Each Reference Matters**:
  - Fatura links a card to an orcamento via `idLancamento` — this is how card bills connect to the monthly budget
  - The `createFatura` request requires `orcamentoId` — this links the fatura to a specific month's budget

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY)**:

  ```
  Scenario: Faturas service compiles correctly
    Tool: Bash
    Steps:
      1. `cd web && npx tsc --noEmit`
      2. Assert zero type errors referencing faturasService
    Expected Result: Clean compilation
    Evidence: .sisyphus/evidence/task-8-faturas-service-compiles.txt

  Scenario: Service uses nested cartaoId routes
    Tool: Bash
    Steps:
      1. Grep faturasService.ts for `{cartaoId}` URL pattern
      2. Verify all 5 functions include cartaoId in the URL path
    Expected Result: All functions use nested route pattern /cartoes/{cartaoId}/faturas/...
    Evidence: .sisyphus/evidence/task-8-nested-routes.txt
  ```

  **Commit**: YES (groups with T8)
  - Message: `feat(web): add faturas API service`
  - Files: `web/src/features/finance/lib/api/faturasService.ts`

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [ ] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, curl endpoint, run command). For each "Must NOT Have": search codebase for forbidden patterns — reject with file:line if found. Check evidence files exist in `.sisyphus/evidence/`. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [ ] F2. **Code Quality Review** — `unspecified-high`
  Run `npm run build` + `npm run lint`. Review all changed files for: `as any`/`@ts-ignore`, empty catches, console.log in prod, commented-out code, unused imports. Check AI slop: excessive comments, over-abstraction, generic names.
  Output: `Build [PASS/FAIL] | Lint [PASS/FAIL] | Files [N clean/N issues] | VERDICT`

- [ ] F3. **Real Manual QA** — `unspecified-high` (+ `playwright` skill)
  Start from clean state. Execute EVERY QA scenario from EVERY task — follow exact steps, capture evidence. Test cross-task integration. Test edge cases: empty state, invalid input, rapid actions. Save to `.sisyphus/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Integration [N/N] | Edge Cases [N tested] | VERDICT`

- [ ] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual diff (git log/diff). Verify 1:1 — everything in spec was built (no missing), nothing beyond spec was built (no creep). Check "Must NOT do" compliance. Detect cross-task contamination.
  Output: `Tasks [N/N compliant] | Contamination [CLEAN/N issues] | Unaccounted [CLEAN/N files] | VERDICT`

---

## Commit Strategy

- **T1**: `feat(api): add ano-mês query param to orcamentos-mensais endpoint`
- **T2**: `feat(web): add API client infrastructure with types and mappers`
- **T3**: `docs(api): update MEMORY.md with accurate frontend integration info`
- **T4**: `feat(web): integrate categorias and cartões API services`
- **T5**: `feat(web): integrate lançamentos fixos API service`
- **T6**: `feat(web): integrate parcelamentos API service`
- **T7**: `feat(web): integrate orçamento mensal and lançamentos mensais API services`
- **T8**: `feat(web): integrate faturas API service`
- **T9**: `refactor(web): create API-based FinanceContext/Provider`
- **T10**: `feat(web): migrate category and card settings UI to API`
- **T11**: `feat(web): migrate despesas/receitas sections and month navigation to API`
- **T12**: `feat(web): migrate parcelamentos and faturas UI to API`
- **T13**: `feat(web): migrate summary and charts to API data model`
- **T14**: `refactor(web): remove IndexedDB/Dexie, MonthOverride system, and buildMonthView`
- **T15**: `test(web): rewrite tests for API-based architecture; docs: update ARCHITECTURE.md`

---

## Success Criteria

### Verification Commands
```bash
cd web && npm run build    # Expected: success, zero errors
cd web && npm test         # Expected: all tests pass
cd server && ./gradlew build  # Expected: success
curl http://localhost:8080/api/orcamentos-mensais?anoMes=2026-05  # Expected: 200 or 404
```

### Final Checklist
- [ ] All "Must Have" present
- [ ] All "Must NOT Have" absent
- [ ] All tests pass
- [ ] Build succeeds
- [ ] Server endpoint responds correctly
- [ ] Frontend loads and interacts with server