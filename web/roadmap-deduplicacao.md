# Roadmap: Remoção de Duplicidade de Código

**Projeto:** controle-financeiro-react
**Base:** 214 testes passando | Zero downtime
**Objetivo:** Eliminar toda duplicidade identificada sem quebrar nenhum componente existente.

> **Regra de ouro:** Cada passo individual deve passar em `npm test` + `npm run build` antes de prosseguir. Se algo quebrar, reverter apenas aquele passo.

---

## Ordem de Execução

| Fase 0     | Fase 1     | Fase 2 | Fase 3 | Fase 4     | Fase 5   | Fase 6   |
| ---------- | ---------- | ------ | ------ | ---------- | -------- | -------- |
| Safety Net | Types      | Utils  | CSS    | Hooks      | Sections | Reducers |
| Zero risco | Zero risco | Baixo  | Médio  | Médio-Alto | Alto     | Alto     |

---

## Fase 0 — Rede de Segurança

**Risco:** Zero

Garantir que temos como detectar regressões antes de tocar em qualquer código duplicado.

| Passo | Ação                                                              |
| ----- | ----------------------------------------------------------------- |
| 0.1   | Rodar `npm test` e salvar baseline (214 passing)                  |
| 0.2   | Rodar `npm run build` e confirmar que compila sem erros           |
| 0.3   | Verificar cobertura de testes em cada arquivo que será modificado |

---

## Fase 1 — Tipos e Constantes

**Risco:** Zero

Extrair definições duplicadas para módulos compartilhados e reimportar. Zero mudança de comportamento.

| Passo | Ação                                                                                                       | Arquivos afetados                                                              |
| ----- | ---------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------ |
| 1.1   | **PieMode:** exportar da definição canônica em `domain/constants.ts` e substituir redeclarações            | `useCharts.ts`, `useSummaryMetrics.ts`, `pieConfig.ts`, `SummaryDashboard.tsx` |
| 1.2   | **ALLOWED_PAYMENT_METHODS:** usar o de `constants.ts`, remover `SPECIAL_PAYMENT_METHODS`                   | `fixedExpenseFormHelpers.ts`                                                   |
| 1.3   | **DEFAULT_CARD_ID = `'outro'`:** criar constante central, substituir ~25 literais `'outro'`                | ~10 arquivos do projeto                                                        |
| 1.4   | **Chart colors:** criar `PAID_COLOR = '#1D9E75'` e `UNPAID_COLOR = '#D85A30'` em `constants.ts`            | `pieConfig.ts`, `installmentBarConfig.ts`                                      |
| 1.5   | **InstallmentItem:** definir em `domain/types.ts`, importar nos 2 locais                                   | `InstallmentsSection.tsx`, `InstallmentRow.tsx`                                |
| 1.6   | **CrudModalState:** unificar as 2 interfaces em `useCrudModalState.ts`, importar em `useCrudFormFlow.ts`   | `useCrudModalState.ts`, `useCrudFormFlow.ts`                                   |
| 1.7   | **CardBills / BillPaymentMap:** substituir por `Record<string, number>` e `Record<string, boolean>` inline | `summarySelectors.ts`                                                          |

**Validação:** `npm test` + `npm run build`

---

## Fase 2 — Funções Utilitárias

**Risco:** Baixo

Extrair funções idênticas para módulos compartilhados. Mudança de import, zero mudança de lógica.

| Passo | Ação                                                                                                                               | Arquivos afetados                                                     |
| ----- | ---------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- |
| 2.1   | **clone:** exportar de `lib/utils.ts`, remover duplicata de `lib/storage.ts`                                                       | `storage.ts`                                                          |
| 2.2   | **resolvePaymentMethod:** mover para `lib/utils.ts`, importar em `FixedExpenseRow.tsx`                                             | `FixedExpenseRow.tsx`, `fixedExpenseFormHelpers.ts`                   |
| 2.3   | **Selectores duplicados:** `selectMonthCardBillAmounts` e `selectMonthCardBills` são idênticos. Remover um, atualizar consumidores | `monthOverrideSelectors.ts` + consumidores                            |
| 2.4   | **cardIconMap:** extrair para `lib/cardIconMap.ts`, importar nas 2 sections                                                        | `FixedExpensesSection.tsx`, `InstallmentsSection.tsx`                 |
| 2.5   | **useMemo cardList:** criar hook `useCardList(cardList?)` ou função utilitária                                                     | `FixedExpensesSection.tsx`, `InstallmentsSection.tsx`, `MonthNav.tsx` |
| 2.6   | **Currency formatters:** consolidar `Intl.NumberFormat` em `lib/currency.ts`                                                       | `moneyInput.ts`, `currency.ts`                                        |
| 2.7   | **Override filter:** extrair `matchOverride()` em `repository.ts`, usar em 3 locais                                                | `repository.ts`, `stateReducers.ts`                                   |

**Validação:** `npm test` + `npm run build`

---

## Fase 3 — CSS

**Risco:** Médio

Consolidar regras duplicadas. Testar visualmente cada componente após cada mudança.

| Passo | Ação                                                                                                                                                 | Arquivos afetados                                                 |
| ----- | ---------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------- |
| 3.1   | **`.bill-input-shell`:** mesclar 5+ declarações em 1 bloco coeso                                                                                     | `month-nav.css`                                                   |
| 3.2   | **`.bill-display.editing` selectors:** mesclar 4+ blocos repetidos                                                                                   | `month-nav.css`                                                   |
| 3.3   | **Media queries `@media (max-width: 900px)`:** consolidar blocos duplicados no próprio `month-nav.css` e verificar sobreposição com `responsive.css` | `month-nav.css`, `responsive.css`                                 |
| 3.4   | **Base button:** criar `.btn` ou usar mixin para os 5 botões com mesmo base style                                                                    | `month-nav.css`, `section-header.css`, `metrics.css`              |
| 3.5   | **Label xs pattern:** criar `.label-xs` utility class para os 4 labels pequenos                                                                      | `section-header.css`, `metrics.css`, `saldo.css`, `month-nav.css` |
| 3.6   | **Summary inline colors:** mover `#A32D2D` e `#854F0B` para CSS variables                                                                            | `Summary.tsx`, CSS variables file                                 |

**Validação:** `npm run dev` + verificação visual + `npm test`

---

## Fase 4 — Form Helpers e CRUD Hooks

**Risco:** Médio-Alto

Criar abstrações genéricas que encapsulam os padrões repetidos, mantendo os arquivos específicos funcionando.

| Passo | Ação                                                                                                | Arquivos afetados                                                                        |
| ----- | --------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| 4.1   | Criar `useCrudState<TForm, TItem>(opts)` genérico com a estrutura comum dos 3 hooks                 | Novo: `shared/useCrudState.ts`                                                           |
| 4.2   | Refatorar `useRevenueCrudState.ts` para usar o genérico                                             | `useRevenueCrudState.ts`                                                                 |
| 4.3   | Refatorar `useFixedExpenseCrudState.ts` para usar o genérico                                        | `useFixedExpenseCrudState.ts`                                                            |
| 4.4   | Refatorar `useInstallmentCrudState.ts` para usar o genérico                                         | `useInstallmentCrudState.ts`                                                             |
| 4.5   | Criar factory `createSectionLabels<T>()` para os 3 objetos de labels                                | `fixedExpenseSectionLabels.ts`, `revenueSectionLabels.ts`, `installmentSectionLabels.ts` |
| 4.6   | Criar `createFormHelpers<T>()` genérico para `create*EmptyForm`, `create*EditForm`, `build*Payload` | 3 arquivos formHelpers                                                                   |
| 4.7   | **Soft-delete pattern:** criar `softDeleteItem(item, currentDate)` em `lib/utils.ts`                | `stateReducers.ts`                                                                       |

**Validação:** `npm test` + `npm run build` + verificação visual dos 3 modais CRUD

---

## Fase 5 — Section Components

**Risco:** Alto

Esta é a refatoração mais complexa. Os 3 sections são 90% boilerplate igual.

| Passo | Ação                                                                                  | Arquivos afetados                   |
| ----- | ------------------------------------------------------------------------------------- | ----------------------------------- |
| 5.1   | Identificar as diferenças reais entre os 3 sections (quais props/renderings divergem) | Análise comparativa                 |
| 5.2   | Criar componente genérico `<CrudSection<T>>` que recebe config como props             | Novo: `shared/CrudSection.tsx`      |
| 5.3   | Migrar `RevenuesSection.tsx` para usar o genérico                                     | `RevenuesSection.tsx`               |
| 5.4   | Migrar `FixedExpensesSection.tsx` para usar o genérico                                | `FixedExpensesSection.tsx`          |
| 5.5   | Migrar `InstallmentsSection.tsx` para usar o genérico                                 | `InstallmentsSection.tsx`           |
| 5.6   | Remover boilerplate morto dos 3 arquivos originais                                    | 3 section files                     |
| 5.7   | Unificar modais `ConfirmModal` e `RuleModal` se possível (compartilhar wrapper JSX)   | `ConfirmModal.tsx`, `RuleModal.tsx` |

**Validação:** `npm test` + `npm run build` + verificação visual completa

---

## Fase 6 — Reducers e Actions

**Risco:** Alto

Os defaults de criação e remoção estão duplicados entre reducers e action hooks.

| Passo | Ação                                                                                                   | Arquivos afetados                          |
| ----- | ------------------------------------------------------------------------------------------------------ | ------------------------------------------ |
| 6.1   | Criar factories: `createDefaultFixedExpense()`, `createDefaultRevenue()`, `createDefaultInstallment()` | Novo: `domain/factories.ts`                |
| 6.2   | Usar factories em `stateReducers.ts` e `useFinanceActions.ts`                                          | `stateReducers.ts`, `useFinanceActions.ts` |
| 6.3   | `createFinanceId` defaults: consolidar spreading de campos defaults                                    | `stateReducers.ts`, `useFinanceActions.ts` |

**Validação:** `npm test` + `npm run build`

---

## Métricas de Sucesso

- 214 testes passando (zero regressões)
- Build compilando sem erros ou warnings novos
- Zero duplicidade de código exato detectada por ferramentas de análise
- Todos os componentes visuais funcionando conforme esperado
- Nenhum componente quebrado ou com comportamento alterado

---

## Resumo de Impacto

| Categoria                   | Instâncias | Severidade  |
| --------------------------- | ---------- | ----------- |
| Código exato/quase exato    | 10         | Alta        |
| Padrões repetidos           | 7          | Média       |
| Tipos/interfaces duplicados | 3          | Média       |
| Constantes duplicadas       | 4          | Baixa-Média |
| CSS duplicado               | 7          | Média-Alta  |
| **Total**                   | **31**     |             |

---

## Auditoria Final Pos-Roadmap

**Data:** 2026-05-13

Depois da conclusao das Fases 4, 5 e 6, a revisao completa do `web` apontou os seguintes pontos residuais:

| Item | Status | Observacao |
| ---- | ------ | ---------- |
| Logs temporarios `dev-server.log` e `dev-server.err.log` | Corrigido | Arquivos removidos de `web`. |
| `console.log` em `ExportButton.tsx` | Corrigido | Log de debug removido do fluxo de exportacao. |
| Casts `as any` introduzidos na migracao para `CrudSection` | Corrigido | `RuleSection`/`CrudSection` tipados genericamente e casts da refatoracao removidos das sections. |
| `package-lock.json` ignorado por `.gitignore` | Ressalva | Pode ser intencional, mas reduz reprodutibilidade de instalacao. Decidir em etapa separada. |
| `*roadmap*` ignorado por `.gitignore` | Ressalva | Faz `ROADMAP_PHASE5_ANALYSIS.md` ficar fora do controle de versao. Decidir se documentacao de roadmap deve ser versionada. |
| Warnings Vite/React plugin (`@vitejs/plugin-react` com Vite 8 / OXC) | Ressalva | Build passa, mas ha warning recorrente. Recomendada decisao separada sobre plugin React/OXC. |

**Plano de correcao imediato:**

1. Remover logs temporarios locais.
2. Remover `console.log` de `ExportButton.tsx`.
3. Fortalecer a tipagem do `CrudSection` e das sections migradas para eliminar casts da refatoracao.
4. Validar com `npm run lint`, `npm test`, `npm run build` e `git diff --check`.

**Validacao apos correcao:**

- `npm run lint`: passou.
- `npm test`: passou, 221 testes.
- `npm run build`: passou.
- `tsc --noEmit --pretty false`: passou.
- `git diff --check`: passou.
