# Memory - Contexto do Projeto

## Projeto
**Nome:** PregsLedger  
**Stack:** Kotlin/Ktor (backend) + React (frontend)  
**Porta:** 8080

## Estrutura do Projeto
```
src/main/kotlin/ledger/orcamento/mensal/
├── api/           # Controllers, DTOs (Request/Response)
├── data/
│   ├── schema/   # Exposed Tables, mapping functions
│   └── repository/ # Data access layer
├── domain/        # Entidades de domínio
└── service/       # Lógica de negócio
migrations/        # Flyway SQL migrations
```

## MVP - Orçamento Mensal

### Status: COMPLETO ✅

### Entidades

**OrcamentoMensal**
- `id`, `idUsuario`, `anoMes` (value object), `slug`, `dataInicio`, `dataFim`
- `seqReceita`, `seqDespesa` - contadores para gerar slugs únicos

**LancamentoMensal**
- `id`, `slug`, `descricao` (max 32), `valor` (Decimal 10,2)
- `tipo`: RECEITA ou DESPESA
- `statusDespesa`: ABERTO, RESERVADO, PAGA (só para DESPESA)

### Slug Format
- Orçamento: `{ano}{mes}` ex: `202602`
- Lançamento: `{tipo}-{ano}-{mes}-{seq}` ex: `R-2026-02-1`, `D-2026-02-1`

### API Endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/orcamentos-mensais/{id}` | Buscar orçamento |
| GET | `/api/orcamentos-mensais/{id}/lancamentos` | Listar lançamentos |
| POST | `/api/orcamentos-mensais/` | Criar orçamento |
| POST | `/api/orcamentos-mensais/{id}/lancamentos` | Criar lançamento |
| PUT | `/api/orcamentos-mensais/{id}/lancamentos/{lancamentoId}` | Atualizar lançamento |
| DELETE | `/api/orcamentos-mensais/{id}` | Excluir orçamento (CASCADE) |
| DELETE | `/api/orcamentos-mensais/{id}/lancamentos/{lancamentoId}` | Excluir lançamento |

### Tech Details
- **ORM:** Exposed (JetBrains)
- **Banco:** PostgreSQL (produção), H2 (testes)
- **Migrations:** Flyway
- **Auth:** Firebase (configurado, mas `idUsuario` está hardcoded como `1`)
- **Serialização:** Kotlinx Serialization

### Bugs conhecidos
- Migration V2 usa sintaxe PostgreSQL (IF NOT EXISTS) - pode falhar com H2

## V1.1 Planejada
- **Contas** (Bancária/Aplicação) - descrição, valor atual, rendimento
- **Aplicação Financeira** - vincular despesa.reservado a uma conta
- **Objetivo** - tipo (Despesa/Objetivo) + percentual
- **Vínculo** (n,n) - Conta ↔ Despesa/Objetivo

## Regras de Negócio
- Lançamentos não existem sem Orçamento Mensal
- ID de apresentação é `tipo-ano-mês-seq` (ex: `R-2026-02-1`)
- ID interno é auto_increment BIGSERIAL
- Ao criar lançamento, incrementa o seq correspondente e gera slug automaticamente
