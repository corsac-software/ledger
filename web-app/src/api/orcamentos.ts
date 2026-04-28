import type {
  CreateLancamentoRequest,
  CreateOrcamentoRequest,
  LancamentoMensal,
  OrcamentoMensal,
  UpdateLancamentoRequest,
} from '@/features/orcamentos/types';

const API_BASE = 'http://localhost:8080/api';

async function fetchApi<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${url}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });

  if (!res.ok) {
    throw new Error(`API Error: ${res.status} ${res.statusText}`);
  }

  return res.json();
}

export const orcamentosApi = {
  getAll: () => fetchApi<OrcamentoMensal[]>('/orcamentos-mensais'),

  getById: (id: number) =>
    fetchApi<OrcamentoMensal>(`/orcamentos-mensais/${id}`),

  create: (data: CreateOrcamentoRequest) =>
    fetchApi<OrcamentoMensal>('/orcamentos-mensais', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  delete: (id: number) =>
    fetchApi<void>(`/orcamentos-mensais/${id}`, { method: 'DELETE' }),

  getLancamentos: (orcamentoId: number) =>
    fetchApi<LancamentoMensal[]>(
      `/orcamentos-mensais/${orcamentoId}/lancamentos`,
    ),

  createLancamento: (orcamentoId: number, data: CreateLancamentoRequest) =>
    fetchApi<LancamentoMensal>(
      `/orcamentos-mensais/${orcamentoId}/lancamentos`,
      { method: 'POST', body: JSON.stringify(data) },
    ),

  updateLancamento: (
    orcamentoId: number,
    lancamentoId: number,
    data: UpdateLancamentoRequest,
  ) =>
    fetchApi<LancamentoMensal>(
      `/orcamentos-mensais/${orcamentoId}/lancamentos/${lancamentoId}`,
      { method: 'PUT', body: JSON.stringify(data) },
    ),

  deleteLancamento: (orcamentoId: number, lancamentoId: number) =>
    fetchApi<void>(
      `/orcamentos-mensais/${orcamentoId}/lancamentos/${lancamentoId}`,
      { method: 'DELETE' },
    ),
};
