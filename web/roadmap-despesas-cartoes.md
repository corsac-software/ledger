# Roadmap - Reorganizacao de Despesas, Cartoes e Faturas

## Contexto

Este roadmap planeja uma mudanca conceitual na navegacao do app:

- A aba **Gastos Fixos** deve evoluir para **Despesas**.
- Despesas deve comportar, no futuro, gastos **fixos** e **variaveis**.
- A area global **Faturas do mes** deve sair do topo fixo da tela e passar para
  uma aba dedicada a **Cartoes**.
- A aba atual **Parcelamentos** deve evoluir para uma area mais ampla de
  **Cartoes**, contendo faturas, cartoes e parcelamentos.

A motivacao e aproximar a organizacao do app da forma como o usuario pensa a
vida financeira:

- Quanto entra?
- Quanto sai?
- O que esta no cartao?
- O mes esta tranquilo ou precisa de atencao?

O objetivo nao e criar contabilidade tecnica, mas uma interface mais simples,
confortavel e orientada a clareza.

---

## Decisao de Produto

### Abas Desejadas

A estrutura alvo recomendada:

1. **Resumo**
2. **Despesas**
3. **Cartoes**
4. **Receitas**

### Por Que Nao Manter "Parcelamentos"

Parcelamento e apenas uma parte do universo de cartoes. A fatura do mes, os
abatimentos, o status de pago/pendente e os parcelamentos pertencem melhor a uma
area chamada **Cartoes**.

Assim, a aba deixa de comunicar uma entidade tecnica e passa a comunicar uma
pergunta real do usuario: "como estao meus cartoes?".

### Por Que "Despesas" no Lugar de "Gastos Fixos"

Gastos fixos sao apenas um tipo de despesa. Quando variaveis entrarem, o nome
atual ficara estreito demais.

**Despesas** permite acomodar:

- fixas;
- variaveis;
- pagas;
- pendentes;
- por categoria;
- por forma de pagamento;
- vinculadas ou nao a cartao.

---

## Principios

1. **Sem big bang de dominio:** primeiro reorganizar navegacao e layout, depois
   evoluir os tipos de dados.
2. **Preservar regras atuais:** soft-delete, overrides mensais, cartoes em uso e
   month view derivada continuam valendo.
3. **Cartao nao e despesa:** cartao e meio/agregador; a despesa pode estar no
   cartao.
4. **Resumo continua executivo:** a tela inicial deve mostrar clareza do mes, nao
   virar central de cadastro.
5. **Despesas variaveis entram como fase propria:** nao misturar com renomeacao
   visual inicial.
6. **Sem mexer fora de `web`:** qualquer arquivo criado ou alterado deve ficar
   dentro da pasta `web`.

---

## Estrutura Alvo

```text
Resumo
  - Saldo previsto
  - Receitas vs despesas
  - Faturas resumidas
  - Distribuicao de despesas
  - Parcelas em aberto
  - Cards de indicadores

Despesas
  - Filtro: Fixas | Variaveis
  - Tabela/lista de despesas fixas
  - Futuramente: despesas variaveis do mes

Cartoes
  - Faturas do mes
  - Status de cada cartao
  - Abatimentos
  - Parcelamentos em aberto
  - Futuramente: compras variaveis no cartao

Receitas
  - Receitas recorrentes
  - Ajustes mensais
```

---

## Fase 0 - Confirmacao de Escopo

**Objetivo:** alinhar nomes, comportamento esperado e riscos antes de editar.

| Passo | Acao                                                                                  |
| ----- | ------------------------------------------------------------------------------------- |
| 0.1   | [feito] Confirmar nomes finais das abas: `Resumo`, `Despesas`, `Cartoes`, `Receitas`. |
| 0.2   | [feito] Confirmar que a area de faturas sai do topo global e entra em `Cartoes`.      |
| 0.3   | [feito] Confirmar que `Parcelamentos` vira subarea dentro de `Cartoes`.               |
| 0.4   | [feito] Usar segmented control `Fixas / Variaveis` antes de variaveis existirem.      |
| 0.5   | [feito] Mapear telas/tests afetados antes de implementar.                             |

**Risco principal:** mover faturas pode deixar o resumo visualmente mais vazio ou
menos imediato. Se isso acontecer, manter cards resumidos de fatura no `Resumo`,
mas nao o cadastro completo.

---

## Fase 1 - Navegacao e Nomes

**Objetivo:** mudar a arquitetura de navegacao sem alterar dominio.

| Passo | Acao                                                                       |
| ----- | -------------------------------------------------------------------------- |
| 1.1   | [feito] Renomear aba `Gastos Fixos` para `Despesas`.                       |
| 1.2   | [feito] Renomear aba `Parcelamentos` para `Cartoes`.                       |
| 1.3   | [feito] Ajustar labels, constantes, testes e textos visiveis relacionados. |
| 1.4   | [feito] Preservar ids internos de aba para nao quebrar estado/navegacao.   |
| 1.5   | [feito] Atualizar `ARCHITECTURE.md` com a nova nomenclatura.               |

**Arquivos provaveis:**

- `src/features/finance/FinanceApp.tsx`
- `src/features/finance/components/app-shell/AppTabs.tsx`
- `src/features/finance/ui/*`
- `src/features/finance/tests/*`
- `ARCHITECTURE.md`

---

## Fase 2 - Despesas Como Evolucao de Gastos Fixos

**Objetivo:** transformar a tela atual de gastos fixos em uma tela de despesas,
mantendo inicialmente o mesmo dominio.

| Passo | Acao                                                                                   |
| ----- | -------------------------------------------------------------------------------------- |
| 2.1   | [feito] Renomear section title para `Despesas Fixas`.                                  |
| 2.2   | [feito] Criar subnavegacao visual `Fixas / Variaveis`.                                 |
| 2.3   | [feito] Manter `Fixas` como conteudo funcional atual.                                  |
| 2.4   | [feito] Deixar `Variaveis` visivel como modo planejado e desabilitado, sem CRUD ainda. |
| 2.5   | [feito] Rever labels de botao para `Nova despesa fixa`.                                |
| 2.6   | [feito] Garantir que selectors e resumo continuem usando os mesmos calculos atuais.    |

**Decisao pendente:** se a tela deve chamar o item de "gasto fixo" dentro da
aba, ou se ja renomeamos tudo para "despesa fixa".

**Arquivos provaveis:**

- `src/features/finance/components/sections/FixedExpensesSection.tsx`
- `src/features/finance/components/sections/fixed-expenses/*`
- `src/features/finance/components/RuleSection.tsx`
- `src/styles/sections/*`

---

## Fase 3 - Criar Aba Cartoes

**Objetivo:** fazer a aba `Cartoes` virar a casa das faturas e parcelamentos.

| Passo | Acao                                                                                              |
| ----- | ------------------------------------------------------------------------------------------------- |
| 3.1   | [feito] Extrair a area `Faturas do mes` para `CardBillsSection.tsx`.                              |
| 3.2   | [feito] Deixar a navegacao mensal principal na topbar global.                                     |
| 3.3   | [feito] Renderizar `Faturas do mes` dentro da aba `Cartoes`.                                      |
| 3.4   | [feito] Renderizar `Parcelamentos` abaixo de faturas na mesma aba.                                |
| 3.5   | [feito] Ajustar spacing por meio do stack `.cards-section`.                                       |
| 3.6   | [feito] Manter todos os fluxos atuais: adicionar cartao, incluir fatura, excluir cartao e status. |

**Arquivos provaveis:**

- `src/features/finance/components/CardBillsSection.tsx`
- `src/features/finance/components/MonthNav.tsx`
- `src/features/finance/components/sections/InstallmentsSection.tsx`
- `src/features/finance/FinanceApp.tsx`
- `src/styles/navigation/month-nav.css`
- `src/styles/sections/*`

---

## Fase 4 - Resumo Sem Cadastro de Faturas no Topo

**Objetivo:** preservar clareza do resumo depois que a area completa de faturas
sair do topo global.

| Passo | Acao                                                                                             |
| ----- | ------------------------------------------------------------------------------------------------ |
| 4.1   | [feito] Remover a area completa `Faturas do mes` do layout global.                               |
| 4.2   | [feito] Manter no `Resumo` apenas leitura agregada das faturas, sem CRUD completo.               |
| 4.3   | [mantido] Cards `Fatura X` continuam como leitura de status no resumo.                           |
| 4.4   | [pendente] Avaliar se o resumo precisa de um CTA discreto para ir para `Cartoes`.                |
| 4.5   | [pendente] Validar visualmente estados sem cartoes, sem fatura, com fatura, negativo e positivo. |

**Risco principal:** o usuario pode perder acesso rapido ao cadastro de fatura.
Mitigacao possivel: card/atalho no resumo para abrir `Cartoes`.

---

## Fase 5 - Despesas Variaveis

**Objetivo:** adicionar o dominio real de despesas variaveis, depois da
reorganizacao visual estar estavel.

| Passo | Acao                                                                                     |
| ----- | ---------------------------------------------------------------------------------------- |
| 5.1   | Definir tipo `VariableExpense` ou unificar em um tipo mais amplo `Expense`.              |
| 5.2   | Decidir se despesas fixas e variaveis compartilham CRUD ou usam sections separadas.      |
| 5.3   | Modelar campos minimos: nome, valor, categoria, pagamento, cartao opcional, status, mes. |
| 5.4   | Criar reducer/factory/normalizer/migration.                                              |
| 5.5   | Incluir variaveis em `buildMonth` e selectors de resumo.                                 |
| 5.6   | Criar UI de cadastro/listagem dentro da aba `Despesas > Variaveis`.                      |
| 5.7   | Atualizar testes de dominio, selectors e UI.                                             |

**Decisao tecnica importante:** variavel deve ser mensal por natureza. Evitar
transformar despesa variavel em recorrente sem necessidade.

---

## Fase 6 - Refinos de Produto

**Objetivo:** lapidar a experiencia depois da mudanca estrutural.

| Passo | Acao                                                                       |
| ----- | -------------------------------------------------------------------------- |
| 6.1   | Revisar microcopy para diferenciar `Despesas`, `Cartoes` e `Receitas`.     |
| 6.2   | Ajustar empty states para orientar sem parecer erro.                       |
| 6.3   | Rever graficos do resumo para incluir variaveis quando existirem.          |
| 6.4   | Avaliar atalhos entre resumo e abas.                                       |
| 6.5   | Validar visual desktop com dados pequenos, medios e extremos.              |
| 6.6   | Deixar validacao mobile para uma rodada futura, conforme decisao anterior. |

---

## Checklist de Gates

Durante a implementacao, usar gates por fase:

- Prettier nos arquivos alterados.
- ESLint em `src`.
- TypeScript com `tsc --noEmit`.
- `git diff --check`.
- Testes/build apenas quando combinarmos explicitamente ou ao fechar uma fase
  maior.

---

## Ordem Recomendada de Execucao

1. **Fase 1:** renomear abas e arquitetura de navegacao.
2. **Fase 2:** transformar Gastos Fixos em Despesas com subaba Fixas.
3. **Fase 3:** mover Faturas do mes para Cartoes e juntar com Parcelamentos.
4. **Fase 4:** reequilibrar o Resumo sem cadastro global de faturas.
5. **Fase 5:** criar Despesas Variaveis de verdade.
6. **Fase 6:** refinar microcopy, empty states e atalhos.

Minha recomendacao e nao iniciar pela Fase 5. Primeiro a interface precisa
provar que a nova organizacao faz sentido; depois o dominio pode crescer com
menos risco.
