# Roadmap - Migracao Visual Inspirada no FinFlow

## Contexto

O `finflow` entrou no projeto como referencia visual externa. Ele e um app
Next/Tailwind separado, com dados mockados e algumas funcionalidades diferentes
da aplicacao atual. Para o `web`, ele deve ser tratado como direcao de layout e
experiencia, nao como fonte de regra de negocio.

Objetivo desta migracao: trazer a sensacao moderna, premium e confortavel do
FinFlow para o front atual, preservando a arquitetura da V1:

- React 18 + Vite do `web`.
- Estado atual via `FinanceContext`, reducers, selectors e hooks.
- Month view sempre derivada.
- CRUDs existentes com modais e overrides mensais.
- Chart.js carregado sob demanda.
- Testes e gates atuais.
- Sem copiar Tailwind, Next, mocks, APIs falsas ou dados estaticos do FinFlow.

---

## O Que o FinFlow Resolve Bem

### 1. Moldura de Produto

- Topbar fixa com marca, mes centralizado e acao global.
- Conteudo com largura maxima mais generosa e margens consistentes.
- Superficies escuras com card elevation melhor definida.
- Layout parece produto acabado, nao apenas uma ferramenta funcional.

### 2. Faturas do Mes

- Bloco de faturas tem mais presenca e melhor organizacao.
- Cards de cartao sao mais largos, respirados e com acao clara.
- `Novo cartao` tem destaque adequado.
- Fatura vazia comunica melhor o estado.

### 3. Tabs

- Tabs parecem controle principal do app.
- Ativo em verde forte facilita orientacao.
- Altura, arredondamento e area clicavel estao mais confortaveis.

### 4. Resumo

- Saldo previsto ganha papel de hero real.
- Cards de receita/despesa estao mais elegantes.
- Empty states de grafico parecem intencionais.
- Icones pequenos ajudam leitura sem virar decoracao pesada.

### 5. Telas CRUD

- `Gastos Fixos` e `Receitas` como tabela sao muito mais escaneaveis.
- `Parcelamentos` como lista de cards com progresso e perfeito para esse dominio.
- Headers de aba com contagem, total e botao primario melhoram contexto.
- Status badges sao mais claros que textos soltos.

### 6. Modal

- Backdrop com blur e escurecimento parece moderno.
- Dialog compacto, centralizado e com acoes claras.
- Campos em superficie escura ficam consistentes com o resto da UI.

---

## O Que Nao Deve Ser Copiado

- Next.js, Tailwind ou estrutura `app/`.
- Dados mockados.
- Fluxos que ignoram regras atuais de soft-delete, overrides e cartao em uso.
- Criacao de fatura com data de vencimento se isso ainda nao existir no dominio.
- Exclusao direta de cartao sem respeitar `useCardDeleteReasons`.
- Logica visual que dependa de classes utilitarias externas.
- Novas regras financeiras sem passar por dominio/selectors.

---

## Mapeamento FinFlow -> Web Atual

| FinFlow               | Web atual                                       | Estrategia                                                       |
| --------------------- | ----------------------------------------------- | ---------------------------------------------------------------- |
| `DashboardTopbar`     | `FinanceApp` + `MonthNav`                       | Separar topbar global da area de faturas.                        |
| `MonthNavigator`      | parte superior de `MonthNav`                    | Mes centralizado no topo sticky.                                 |
| `TopbarActions`       | `ExportButton` + toggle tema                    | Mover para topbar global com botoes mais premium.                |
| `CreditCardsSection`  | `MonthNav`                                      | Manter estado atual, aplicar layout/card visual do FinFlow.      |
| `DashboardTabs`       | `AppTabs`                                       | Reestilizar tabs com ativo verde e altura confortavel.           |
| `SaldoPrevistoCard`   | `Summary.tsx` / `saldo.css`                     | Adaptar hero sem alterar calculos.                               |
| `ReceitasDespesasRow` | cards de `Summary.tsx`                          | Reestilizar support cards com icones/status discreto.            |
| `FaturaStatusRow`     | `billCardsSummary` em `Summary.tsx`             | Melhorar cards de resumo de fatura sem duplicar logica.          |
| `DistribuicaoSection` | `SummaryDashboard.tsx` + `charts-layout.css`    | Manter Chart.js e empty states atuais, elevar acabamento visual. |
| `ParcelasEmAberto`    | `SummaryDashboard.tsx` + `installmentBarConfig` | Manter regras hibridas: chart para muitos, insight para poucos.  |
| `GastosFixosTab`      | `FixedExpensesSection` + `RuleSection`          | Reestilizar tabela/lista sem quebrar CRUD compartilhado.         |
| `ParcelamentosTab`    | `InstallmentsSection`                           | Trocar visual para cards de progresso, mantendo actions atuais.  |
| `ReceitasTab`         | `RevenuesSection`                               | Adicionar cards de resumo e tabela mais refinada.                |
| `IncluirFaturaModal`  | input inline em `BillCard` + modais atuais      | Avaliar se vira modal depois; primeira fase mantem input atual.  |

---

## Principios de Migracao

1. **Layout antes de regra:** mudar composicao e CSS, nao dominio.
2. **Um eixo por fase:** topbar, faturas, resumo, CRUDs e modais em fases separadas.
3. **Sem big bang:** cada fase precisa passar Prettier, ESLint, typecheck, testes e build.
4. **Mobile nao e objetivo principal agora:** evitar regressao grosseira, mas nao redesenhar mobile completo.
5. **Tema claro precisa sobreviver:** o FinFlow e forte no escuro; adaptar tokens para os dois temas.
6. **FinFlow e referencia, nao dependencia:** nenhuma classe Tailwind ou componente Next deve entrar no `web`.
7. **Preservar testabilidade:** textos importantes, estados vazios e interacoes devem continuar cobertos.

---

## Fase 0 - Auditoria e Tokens

**Objetivo:** preparar a base visual sem tocar comportamento.

| Passo | Acao                                                                                                                               |
| ----- | ---------------------------------------------------------------------------------------------------------------------------------- |
| 0.1   | [feito] Extrair do FinFlow os tokens visuais relevantes: background, card, border, primary, muted, warning, info.                  |
| 0.2   | [feito] Mapear esses tokens para `src/styles/theme/tokens.css` nos temas `premium` e `default`.                                    |
| 0.3   | [parcial] Criar/ajustar classes compartilhadas de superficie: card surface, elevated, primary button, subtle button, status badge. |
| 0.4   | [feito] Garantir que nomes e tokens sigam `ARCHITECTURE.md`, sem Tailwind.                                                         |

**Arquivos provaveis:**

- `src/styles/theme/tokens.css`
- `src/styles/shared/*.css`
- `src/styles/core/app.css`
- `ARCHITECTURE.md`

---

## Fase 1 - Moldura Global e Topbar

**Objetivo:** trazer a sensacao de produto acabado.

| Passo | Acao                                                                              |
| ----- | --------------------------------------------------------------------------------- |
| 1.1   | [feito] Separar visualmente topbar global da area de faturas.                     |
| 1.2   | [feito] Criar topbar sticky com marca/app name, mes centralizado e acoes globais. |
| 1.3   | [feito] Reposicionar `ExportButton` e toggle de tema com estilo FinFlow-like.     |
| 1.4   | [feito] Ajustar largura maxima e padding geral para desktop mais premium.         |
| 1.5   | [feito] Validar que troca de mes, tema e exportacao continuam funcionando.        |

**Arquivos provaveis:**

- `src/features/finance/FinanceApp.tsx`
- `src/features/finance/components/MonthNav.tsx`
- `src/features/finance/components/ExportButton.tsx`
- `src/styles/core/app.css`
- `src/styles/navigation/month-nav.css`

---

## Fase 2 - Faturas do Mes

**Objetivo:** aplicar a presenca visual do FinFlow mantendo regras atuais.

| Passo | Acao                                                                                 |
| ----- | ------------------------------------------------------------------------------------ |
| 2.1   | [feito] Transformar a area de faturas em card surface amplo e mais respirado.        |
| 2.2   | [feito] Reestilizar cards de cartao com largura, altura, badge e botao mais claros.  |
| 2.3   | [feito] Melhorar estados `sem fatura`, `pendente`, `paga` e `em uso`.                |
| 2.4   | [feito] Preservar input/animacao de fatura atual ate decidir por modal.              |
| 2.5   | [feito] Garantir que `useCardDeleteReasons` continue bloqueando exclusoes invalidas. |

**Arquivos provaveis:**

- `src/features/finance/components/MonthNav.tsx`
- `src/styles/navigation/month-nav.css`
- `src/features/finance/tests/*MonthNav*` se existir ou novos testes focados

---

## Fase 3 - Tabs e Navegacao de Secoes

**Objetivo:** deixar tabs mais claras e clicaveis.

| Passo | Acao                                                                                   |
| ----- | -------------------------------------------------------------------------------------- |
| 3.1   | [feito] Reestilizar `AppTabs` como segmented control forte.                            |
| 3.2   | [feito] Usar verde/accent apenas no ativo, mantendo inativos discretos.                |
| 3.3   | [feito] Revisar spacing entre faturas, tabs e conteudo.                                |
| 3.4   | [feito] Validar navegacao entre `Resumo`, `Gastos Fixos`, `Parcelamentos`, `Receitas`. |

**Arquivos provaveis:**

- `src/features/finance/components/app-shell/AppTabs.tsx`
- `src/styles/navigation/tabs.css` ou equivalente
- `src/styles/core/app.css`

---

## Fase 4 - Resumo FinFlow, Sem Perder Clareza Financeira

**Objetivo:** elevar o resumo visual sem desfazer o trabalho de clareza.

| Passo | Acao                                                                            |
| ----- | ------------------------------------------------------------------------------- |
| 4.1   | [feito] Reestilizar saldo previsto como hero card com glow discreto por estado. |
| 4.2   | [limitado] Adicionar icones pequenos apenas quando reforcarem status/acao.      |
| 4.3   | [feito] Reestilizar cards de receitas/despesas com altura e hierarquia FinFlow. |
| 4.4   | [feito] Reestilizar cards de fatura do resumo como linha elegante de status.    |
| 4.5   | [feito] Manter microcopy atual: mes confortavel, apertado e negativo.           |
| 4.6   | [feito] Preservar alertas orientativos sem virar UI alarmista.                  |

**Arquivos provaveis:**

- `src/features/finance/components/Summary.tsx`
- `src/styles/sections/saldo.css`
- `src/styles/sections/metrics.css`
- `src/styles/sections/alerts.css`
- `src/features/finance/tests/Summary.test.tsx`

---

## Fase 5 - Graficos e Empty States

**Objetivo:** unir o acabamento do FinFlow com a regra hibrida ja decidida.

| Passo | Acao                                                                            |
| ----- | ------------------------------------------------------------------------------- |
| 5.1   | [feito] Reestilizar cards de graficos com acabamento FinFlow.                   |
| 5.2   | [feito] Melhorar empty states com icones discretos e textos mais centralizados. |
| 5.3   | [feito] Manter regra: grafico para muitos dados, insight compacto para poucos.  |
| 5.4   | [feito] Revisar cores dos charts para dialogar com a nova paleta.               |
| 5.5   | [feito] Validar categorias, cartoes, pago/pendente, sem dados e poucos dados.   |

**Arquivos provaveis:**

- `src/features/finance/components/summary/SummaryDashboard.tsx`
- `src/features/finance/components/summary/ChartEmpty.tsx`
- `src/styles/visual/charts-layout.css`
- `src/features/finance/lib/charts/chartTheme.ts`
- `src/features/finance/tests/SummaryDashboard.test.tsx`
- `src/features/finance/tests/chartConfigs.test.ts`

---

## Fase 6 - Gastos Fixos Como Tabela Premium

**Objetivo:** trazer a escaneabilidade da tabela do FinFlow para os dados reais.

| Passo | Acao                                                                            |
| ----- | ------------------------------------------------------------------------------- |
| 6.1   | [parcial] Adicionar header da aba com titulo, contagem, total e botao primario. |
| 6.2   | [feito] Reestilizar `RuleSection`/rows para parecer tabela premium.             |
| 6.3   | [feito] Usar badges para pago/pendente.                                         |
| 6.4   | [feito] Manter edicao, delete, override mensal e toggle pago atuais.            |
| 6.5   | [parcial] Garantir responsividade minima sem redesenhar mobile completo.        |

**Arquivos provaveis:**

- `src/features/finance/components/sections/FixedExpensesSection.tsx`
- `src/features/finance/components/RuleSection.tsx`
- `src/features/finance/components/sections/shared/*`
- `src/styles/sections/*.css`

---

## Fase 7 - Parcelamentos Como Cards de Progresso

**Objetivo:** adotar o melhor padrao do FinFlow para parcelamentos.

| Passo | Acao                                                                               |
| ----- | ---------------------------------------------------------------------------------- |
| 7.1   | [feito] Criar visual de card por parcelamento com titulo, categoria/cartao/inicio. |
| 7.2   | [feito] Mostrar valor mensal, parcela atual/total e progresso.                     |
| 7.3   | [feito] Destacar `Perto de quitar` quando >= 75%.                                  |
| 7.4   | [feito] Preservar CRUD, delete, soft-delete e toggle pago atuais.                  |
| 7.5   | [parcial] Cobrir cenarios vazio, 1 item e muitos itens.                            |

**Arquivos provaveis:**

- `src/features/finance/components/sections/InstallmentsSection.tsx`
- `src/features/finance/components/sections/shared/*`
- `src/styles/sections/*.css`

---

## Fase 8 - Receitas Com Resumo Superior

**Objetivo:** trazer contexto antes da tabela.

| Passo | Acao                                                                                                             |
| ----- | ---------------------------------------------------------------------------------------------------------------- |
| 8.1   | [parcial] Criar header da aba com contagem e botao `Nova receita`.                                               |
| 8.2   | [parcial] Adicionar cards de resumo: total do mes, recebido/realizado se existir, a receber/previsto se existir. |
| 8.3   | [parcial] Reestilizar tabela de receitas com badges e acoes discretas.                                           |
| 8.4   | [feito] Nao inventar status novo se o dominio atual nao armazenar recebido/previsto.                             |
| 8.5   | [feito] Validar overrides mensais de valor.                                                                      |

**Arquivos provaveis:**

- `src/features/finance/components/sections/RevenuesSection.tsx`
- `src/features/finance/components/sections/shared/*`
- `src/styles/sections/*.css`

---

## Fase 9 - Modais e Formularios

**Objetivo:** aplicar acabamento FinFlow aos modais existentes.

| Passo | Acao                                                                          |
| ----- | ----------------------------------------------------------------------------- |
| 9.1   | [feito] Reestilizar backdrop com blur/escurecimento.                          |
| 9.2   | [feito] Reestilizar `ModalShell`, inputs e botoes de acao.                    |
| 9.3   | [feito] Manter formularios atuais e validacoes atuais.                        |
| 9.4   | [feito] Avaliar depois se fatura por cartao deve migrar de inline para modal. |
| 9.5   | [parcial] Validar foco, escape, cancelamento e confirmacao.                   |

**Arquivos provaveis:**

- `src/features/finance/components/modals/*`
- `src/features/finance/components/inputs/*`
- `src/styles/forms/*`
- `src/styles/shared/*`

---

## Fase 10 - Validacao Final

| Passo | Acao                                                                         |
| ----- | ---------------------------------------------------------------------------- |
| 10.1  | [feito] Prettier nos arquivos alterados.                                     |
| 10.2  | [feito] `npm run lint`.                                                      |
| 10.3  | [feito] `tsc --noEmit --pretty false`.                                       |
| 10.4  | [feito] `npm test`.                                                          |
| 10.5  | [feito] `npm run build`.                                                     |
| 10.6  | [feito] `git diff --check`.                                                  |
| 10.7  | [limitado] Revisao visual manual desktop nos 4 tabs e modal.                 |
| 10.8  | [limitado] Passada mobile apenas para regressao grosseira.                   |
| 10.9  | [feito] Atualizar `ARCHITECTURE.md` com novos padroes visuais reutilizaveis. |

---

## Ordem Recomendada de Execucao

1. Fase 0: tokens e surfaces.
2. Fase 1: topbar e moldura global.
3. Fase 3: tabs.
4. Fase 2: faturas.
5. Fase 4 e 5: resumo e graficos.
6. Fase 7: parcelamentos, maior ganho visual por menor risco estrutural.
7. Fase 6: gastos fixos.
8. Fase 8: receitas.
9. Fase 9: modais.
10. Fase 10: validacao final.

Essa ordem maximiza impacto visual cedo e deixa as telas CRUD mais complexas para
quando a linguagem visual ja estiver estabilizada.

---

## Riscos e Cuidados

- `RuleSection` e `CrudSection` sao compartilhados; mudancas ali afetam mais de
  uma aba.
- Parcelamentos podem exigir layout especializado sem destruir o CRUD generico.
- Tema claro pode ficar fraco se copiarmos apenas a paleta escura do FinFlow.
- O app atual ja tem regras de fatura por cartao mais especificas que o FinFlow.
- A tentacao de copiar mocks deve ser evitada: todos os dados devem continuar
  vindo de `monthView`, `settings`, `monthOverrides` e hooks atuais.
- Icones devem ser introduzidos com parcimonia e preferencialmente via biblioteca
  ja instalada ou uma decisao explicita de dependencia.

---

## Criterio de Aceite

- O primeiro olhar deve parecer tao moderno quanto o FinFlow.
- O usuario deve entender rapidamente onde esta, qual mes esta vendo e qual acao
  principal pode tomar.
- O resumo deve continuar transmitindo tranquilidade, nao contabilidade.
- CRUDs devem ficar mais escaneaveis sem perder funcoes atuais.
- Nenhum calculo, persistencia, soft-delete, override ou exportacao pode regredir.
- Todos os gates tecnicos devem passar.

---

## Fechamento

Roadmap encerrado para implementacao automatizada nesta branch. A migracao trouxe
a linguagem visual do FinFlow para o `web` sem copiar Next/Tailwind, mocks ou
regras de negocio externas.

Pontos deliberadamente limitados:

- `Gastos Fixos` e `Receitas` usam tabela premium compartilhada, mas nao ganharam
  novos status de dominio.
- Fatura por cartao permanece inline; a migracao para modal foi avaliada e
  deixada fora para nao alterar fluxo validado.
- Validacao visual desktop/mobile ainda depende de revisao manual no browser.

---

## Acabamento Pos-Fechamento

Depois da comparacao direta com os prints do FinFlow, foi aplicada uma rodada de
acabamento visual focada apenas no tema escuro:

- Topbar virou uma barra mais fina de app, com conteudo centralizado pelo mesmo
  limite visual do dashboard.
- Dashboard passou a usar largura maxima controlada por `--app-max-width`.
- Area de faturas ganhou mais altura, respiro e proporcao de cards mais proxima
  do FinFlow.
- Tabs, botoes principais e botoes de modais ficaram mais baixos/arredondados.
- Tabelas ganharam linhas mais altas, bordas/radius mais premium e botao de
  criacao verde por padrao.
- Cards de parcelamentos ficaram mais altos e com progresso mais parecido com o
  layout de referencia.
- Cards de resumo, saldo e graficos receberam pequenos ajustes de escala,
  padding e hierarquia.

Validado sem `vitest`/`build`, conforme decisao de fluxo desta etapa, com:
Prettier, ESLint, `tsc --noEmit --pretty false` e `git diff --check`.
