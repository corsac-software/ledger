import { createFileRoute } from '@tanstack/react-router';
import OrcamentoDetail from '@/features/orcamentos/components/OrcamentoDetail';

export const Route = createFileRoute('/orcamentos/$id')({
  component: OrcamentoDetail,
});
