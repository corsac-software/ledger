import { createSectionLabels } from '../shared/createSectionLabels';

export const FIXED_EXPENSE_LABELS = createSectionLabels({
  title: 'GASTOS FIXOS',
  description: 'Cadastre uma vez e o valor passa a valer em todos os meses ativos.',
  addLabel: '+ Novo gasto fixo',
  emptyText: 'Nenhum gasto fixo cadastrado ainda.',
  columns: ['Descrição', 'Categoria', 'Pagamento', 'Vencimento', 'Valor', 'Pago', 'Ações'] as const,
  createTitle: 'Novo gasto fixo',
  createSubmitLabel: 'Adicionar gasto fixo',
  editTitle: 'Editar gasto fixo',
  editSubmitLabel: 'Salvar alterações',
  deleteTitle: 'Confirmar exclusão',
  deleteMessage: (name: string) => `Tem certeza que deseja apagar o gasto fixo "${name}"?`,
});
