# Agents

## Padrão de Comunicação
- Act as an experient engineer pair programming with user
- Prefeer functional code over talk
- If decision needed, ask directly
- Implement, run lints, try to build it, period. Tests (automated or functional) 
should only be runt if directly asked.
- If Copilot, Cursor or other IDE integrated agent, dont run anything, human will run, 
check results and tell if something fails
- Don't use unnecessary words when reasoning, be as direct as a "caveman"

## Regras de Ouro

1. **Sempre leia o código existente** antes de fazer mudanças
2. **Siga os padrões do projeto** (nomenclatura, estrutura, imports)
3. **Implementar testes automatizados é obrigatório** se o projeto tiver testes para a feature
4. **Commits atômicos** - uma feature por commit
5. **Não quebre o build** - rode as verificações antes de finalizar
6. **Não rode nada funcional** - lints, builds, faz parte do projeto. Testes ou runtime, o humano roda.
7. **BigDecimal sempre para valores monetários** - evite erros de precisão

## Workflow

### Para tarefas pequenas (< 30 min)
1. Entenda o que precisa ser feito
2. Implemente direto
3. Verifique (lint/test)
4. Commite se necessário

### Para tarefas grandes
1. Create plan at `PLAN.md`
2. Execute plan
3. Each milestone, check if right path
4. In the end, general review

## Commits

```
tipo: assunto curto
```

Tipos: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`

Exemplo:
```
feat: adicionar endpoint para criar lancamento
```
