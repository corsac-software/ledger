# Session Progress - Deduplicacao Web

Data: 2026-05-12

## Contexto recebido

- Usuario pediu continuar o roadmap em `web/roadmap-deduplicacao.md`.
- Resumo informado pelo usuario dizia que a IA anterior parou apos aplicar o passo 2.2 e antes de validar.
- Regra do roadmap: cada passo deve passar em `npm test` + `npm run build` antes de prosseguir.

## Estado confirmado

- Roadmap correto lido: `web/roadmap-deduplicacao.md`.
- Fase atual: Fase 2 - Funcoes Utilitarias.
- Passo 2.1 (`clone`) ja esta aplicado:
  - `lib/utils.ts` exporta `clone`.
  - `lib/storage.ts` importa `clone` de `./utils.js`.
- Passo 2.2 (`resolvePaymentMethod`) ja esta aplicado no codigo:
  - `lib/utils.ts` exporta `resolvePaymentMethod`.
  - `FixedExpenseRow.tsx` importa `resolvePaymentMethod`.
  - `fixedExpenseFormHelpers.ts` importa `resolvePaymentMethod`.

## Proximo trabalho

1. Validar passo 2.2 com `npm test` e `npm run build`.
2. Se passar, executar passo 2.3:
   - `selectMonthCardBillAmounts` e `selectMonthCardBills` em `selectors/monthOverrideSelectors.ts` sao duplicados.
   - Remover um seletor e atualizar consumidores/testes.
3. Validar passo 2.3 com `npm test` e `npm run build`.

## Log

- Criado este arquivo para permitir continuidade se a sessao terminar por falta de tokens.
- Tentativa inicial de validar 2.2 com `npm test` e `npm run build` no PowerShell falhou porque `npm` nao estava no PATH.
- Tentativa com Node bundlado chamando `vitest`/`vite` diretamente falhou com `spawn EPERM` ao carregar configs.
- Usuario orientou usar `bash` em vez de PowerShell.
- Git Bash encontrado em `C:\Program Files\Git\bin\bash.exe`; dentro do sandbox falhou com `couldn't create signal pipe, Win32 error 5`.
- Rodado via Git Bash fora do sandbox:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
- Passo 2.2 validado. Proximo: aplicar 2.3.
- Passo 2.3 aplicado:
  - Removido `selectMonthCardBills` de `selectors/monthOverrideSelectors.ts`.
  - `selectMonthCardBillAmounts` mantido como seletor canonico.
  - Atualizados `monthOverrideSelectors.test.ts` e `performanceFull.test.ts`.
  - Busca por `selectMonthCardBills` em `src` nao retornou referencias.
- Passo 2.3 validado via Git Bash fora do sandbox:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
- Proximo: aplicar passo 2.4 (`cardIconMap` compartilhado).
- Passo 2.4 aplicado:
  - Criado `src/features/finance/lib/cardIconMap.ts`.
  - `FixedExpensesSection.tsx` usa `buildCardIconMap(cardList ?? [])`, preservando `CARD_ICONS` como base.
  - `InstallmentsSection.tsx` usa `buildCardIconMap(cards, {})`, preservando comportamento anterior sem base defaults local.
- Passo 2.4 validado via Git Bash fora do sandbox:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
- Proximo: aplicar passo 2.5 (`useMemo cardList` compartilhado).
- Passo 2.5 aplicado:
  - Criado `src/features/finance/hooks/useCardList.ts`.
  - `MonthNav.tsx`, `FixedExpensesSection.tsx` e `InstallmentsSection.tsx` usam `useCardList(cardList)`.
  - Busca pelo padrao antigo em `src/features/finance` ficou restrita ao proprio hook.
- Passo 2.5 validado via Git Bash fora do sandbox:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
- Proximo: analisar e aplicar passo 2.6 (`Currency formatters`).
- Passo 2.6 aplicado:
  - `moneyInputFormatter` movido para `src/features/finance/lib/currency.ts`.
  - `moneyInput.ts` importa o formatter de `currency.ts`.
  - Busca por `new Intl.NumberFormat` em `lib` ficou concentrada em `currency.ts`.
- Passo 2.6 validado via Git Bash fora do sandbox:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
- Proximo: analisar e aplicar passo 2.7 (`matchOverride`).
- Passo 2.7 aplicado:
  - Criado `matchOverride` em `src/features/finance/domain/overrides/repository.ts`.
  - `filterByMonthAndType` e `findOverride` usam `matchOverride`.
  - `stateReducers.ts` usa `matchOverride` em `removeInstallment`, `upsertMonthOverride` e `clearMonthOverride`.
  - Busca por comparacoes antigas de `override.type/itemId/monthKey` nos arquivos alvo nao retornou resultados.
- Passo 2.7 validado via Git Bash fora do sandbox:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
- Fase 2 concluida integralmente.
- Proximo: iniciar analise da Fase 3 (CSS), comecando por `month-nav.css`.
- Fase 3 iniciada.
- Passo 3.1 aplicado:
  - Consolidada a repeticao de `.bill-input-shell` em `src/styles/navigation/month-nav.css`.
  - Removidos blocos duplicados de `.bill-display.editing ...` e `.bill-card-static` sem uso.
  - Mantidos estados: `.bill-input-shell.visible`, `.bill-display.editing .bill-input-shell`, `.bill-input-shell:focus-within` e `.bill-card .bill-input-shell`.
- Passo 3.1 validado:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
  - Dev server iniciado em `http://127.0.0.1:5173/` usando `node.exe node_modules/vite/bin/vite.js`.
  - Checagem visual desktop e mobile com Chrome headless:
    - Estado sem cartao renderizou corretamente.
    - Estado com cartao e input de fatura aberto renderizou sem sobreposicao aparente.
- Dev server encerrado e artefatos temporarios removidos.
- Passo 3.2 conferido:
  - A consolidacao do 3.1 tambem removeu os blocos repetidos de `.bill-display.editing`.
  - Restaram somente tres seletores de estado: moeda, valor/botao e input shell.
- Passo 3.3 aplicado e validado:
  - `month-nav.css` agora possui um unico `@media (max-width: 900px)`.
  - `responsive.css` mantem seu proprio bloco global; sem sobreposicao alterada.
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
  - Captura mobile com cartao e input de fatura aberto renderizou sem sobreposicao aparente.
  - Dev server encerrado e artefatos temporarios removidos.
- Passo 3.4 aplicado:
  - Criada base CSS agrupada em `section-header.css` usando `:where(.add-btn, .month-step-btn, .theme-btn, .card-bill-add-btn, .card-bill-empty-btn, .bill-pay-btn)`.
  - Removidas declaracoes repetidas de display/alinhamento/borda/radius/background/cor/cursor em `month-nav.css`, `metrics.css` e `section-header.css`.
  - Mantidas variacoes especificas de tamanho, borda e estados pagos/pendentes.
- Passo 3.4 validado:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
  - Checagem visual desktop com cartao criado confirmou botoes principais alinhados.
  - Dev server encerrado e artefatos temporarios removidos.
- Proximo: aplicar passo 3.5 (`Label xs pattern`).
- Passo 3.5 aplicado:
  - Criada utilidade `.label-xs` em `section-header.css`.
  - A base tambem cobre `.sec-title`, `.mcard-label`, `.saldo-label`, `.card-bill-title` e `.bill-card-label` por agrupamento.
  - Usadas variaveis CSS para preservar tamanhos/cores/espaçamentos especificos.
  - Removidas declaracoes repetidas de labels em `metrics.css`, `saldo.css` e `month-nav.css`.
- Passo 3.5 validado:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
  - Checagem visual desktop confirmou labels principais legiveis e alinhados.
  - Dev server encerrado e artefatos temporarios removidos.
- Proximo: aplicar passo 3.6 (`Summary inline colors`).
- Passo 3.6 aplicado:
  - Criados tokens `--color-summary-paid` e `--color-summary-payable` em `src/styles/theme/tokens.css`.
  - Criadas classes `.saldo-detail-paid` e `.saldo-detail-payable` em `src/styles/sections/saldo.css`.
  - `Summary.tsx` deixou de usar cores inline para os detalhes de pago/a pagar.
- Passo 3.6 validado via Git Bash fora do sandbox:
  - `npm test`: 22 test files passed, 219 tests passed.
  - `npm run build`: build concluido com sucesso.
- Fase 3 concluida conforme roadmap.
- Observacao: ha ruido potencial de final de linha em alguns CSS no diff normal; o diff com `--ignore-space-at-eol` mostra apenas as mudancas reais esperadas.
- Higiene pos-ressalvas aplicada:
  - Criado `web/.editorconfig` para padronizar arquivos do projeto web com LF, newline final e indentacao de 2 espacos.
  - Criado `src/styles/shared/controls.css` para centralizar utilidades compartilhadas de botoes e labels.
  - `src/styles.css` importa `shared/controls.css` antes dos modulos especificos; `section-header.css` voltou a conter apenas regras da secao.
  - Script `format` de `package.json` agora inclui CSS e `src/styles.css`.
  - `ErrorBoundary.test.tsx` silencia apenas os erros esperados do teste de boundary, removendo stack traces do output.
- Higiene pos-ressalvas validada:
  - `npm test`: 22 test files passed, 219 tests passed, sem stack traces esperados do ErrorBoundary no output.
  - `npm run build`: build concluido com sucesso.
- Husky/pre-commit corrigido:
  - `git config core.hooksPath` configurado no repositorio raiz para `web/.husky`.
  - `web/.husky/pre-commit` agora possui shebang, entra em `web` e roda `npm run lint && npm run test`.
  - Script `prepare` de `package.json` agora reconfigura `core.hooksPath` para `web/.husky`, adequado ao repo raiz `ledger` com app em subpasta.
  - Validado com `npm run prepare` e `git hook run pre-commit`: lint passou e 219 testes passaram.
- Limpeza de package/test setup:
  - Removido `lint-staged` e `.lintstagedrc.json`, pois o pre-commit atual roda lint/test do projeto web diretamente.
  - Consolidado `jest-dom` em `vitest.setup.ts` usando `@testing-library/jest-dom/vitest`.
  - Removido `src/features/finance/tests/setupTests.ts` e simplificado `setupFiles` em `vitest.config.ts`.
  - Validado com `npm run lint`, `npm test` (219 testes) e `npm run build`.
