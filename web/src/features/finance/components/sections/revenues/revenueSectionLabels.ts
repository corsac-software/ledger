import { createSectionLabels } from '../shared/createSectionLabels';

export const REVENUE_LABELS = createSectionLabels({
  title: 'RECEITAS',
  description:
    'A receita se repete por mês e pode receber ajuste específico depois, se necessário.',
  addLabel: '+ Nova receita',
  emptyText: 'Nenhuma receita cadastrada ainda.',
  columns: ['Nome', 'Valor', 'Desde', 'Ações'] as const,
  createTitle: 'Nova receita',
  createSubmitLabel: 'Adicionar receita',
  editTitle: 'Editar receita',
  editSubmitLabel: 'Salvar alterações',
  deleteTitle: 'Confirmar exclusão',
  deleteMessage: (name: string) => `Tem certeza que deseja apagar a receita "${name}"?`,
});
