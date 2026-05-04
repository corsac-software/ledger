import { createFileRoute } from '@tanstack/react-router';
import OrcamentosList from '@/features/orcamentos/components/OrcamentosList';

export const Route = createFileRoute('/orcamentos/_index')({
  component: OrcamentosList,
});
