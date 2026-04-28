import { create } from 'zustand';
import { orcamentosApi } from '@/api/orcamentos';
import type {
  CreateLancamentoRequest,
  CreateOrcamentoRequest,
  LancamentoMensal,
  OrcamentoMensal,
  UpdateLancamentoRequest,
} from '@/features/orcamentos/types';

interface OrcamentosState {
  orcamentos: OrcamentoMensal[];
  selectedOrcamento: OrcamentoMensal | null;
  lancamentos: LancamentoMensal[];
  loading: boolean;
  error: string | null;

  fetchOrcamentos: () => Promise<void>;
  fetchOrcamento: (id: number) => Promise<void>;
  fetchLancamentos: (orcamentoId: number) => Promise<void>;
  createOrcamento: (data: CreateOrcamentoRequest) => Promise<void>;
  deleteOrcamento: (id: number) => Promise<void>;
  createLancamento: (
    orcamentoId: number,
    data: CreateLancamentoRequest,
  ) => Promise<void>;
  updateLancamento: (
    orcamentoId: number,
    lancamentoId: number,
    data: UpdateLancamentoRequest,
  ) => Promise<void>;
  deleteLancamento: (orcamentoId: number, lancamentoId: number) => Promise<void>;
}

export const useOrcamentosStore = create<OrcamentosState>((set) => ({
  orcamentos: [],
  selectedOrcamento: null,
  lancamentos: [],
  loading: false,
  error: null,

  fetchOrcamentos: async () => {
    set({ loading: true, error: null });
    try {
      const orcamentos = await orcamentosApi.getAll();
      set({ orcamentos, loading: false });
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  fetchOrcamento: async (id: number) => {
    set({ loading: true, error: null });
    try {
      const orcamento = await orcamentosApi.getById(id);
      set({ selectedOrcamento: orcamento, loading: false });
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  fetchLancamentos: async (orcamentoId: number) => {
    set({ loading: true, error: null });
    try {
      const lancamentos = await orcamentosApi.getLancamentos(orcamentoId);
      set({ lancamentos, loading: false });
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  createOrcamento: async (data: CreateOrcamentoRequest) => {
    set({ loading: true, error: null });
    try {
      const novo = await orcamentosApi.create(data);
      set((state) => ({
        orcamentos: [...state.orcamentos, novo],
        loading: false,
      }));
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  deleteOrcamento: async (id: number) => {
    set({ loading: true, error: null });
    try {
      await orcamentosApi.delete(id);
      set((state) => ({
        orcamentos: state.orcamentos.filter((o) => o.id !== id),
        selectedOrcamento: state.selectedOrcamento?.id === id ? null : state.selectedOrcamento,
        lancamentos: state.selectedOrcamento?.id === id ? [] : state.lancamentos,
        loading: false,
      }));
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  createLancamento: async (orcamentoId: number, data: CreateLancamentoRequest) => {
    set({ loading: true, error: null });
    try {
      const novo = await orcamentosApi.createLancamento(orcamentoId, data);
      set((state) => ({
        lancamentos: [...state.lancamentos, novo],
        loading: false,
      }));
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  updateLancamento: async (
    orcamentoId: number,
    lancamentoId: number,
    data: UpdateLancamentoRequest,
  ) => {
    set({ loading: true, error: null });
    try {
      const atualizado = await orcamentosApi.updateLancamento(
        orcamentoId,
        lancamentoId,
        data,
      );
      set((state) => ({
        lancamentos: state.lancamentos.map((l) =>
          l.id === lancamentoId ? atualizado : l,
        ),
        loading: false,
      }));
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },

  deleteLancamento: async (orcamentoId: number, lancamentoId: number) => {
    set({ loading: true, error: null });
    try {
      await orcamentosApi.deleteLancamento(orcamentoId, lancamentoId);
      set((state) => ({
        lancamentos: state.lancamentos.filter((l) => l.id !== lancamentoId),
        loading: false,
      }));
    } catch (e) {
      set({ error: (e as Error).message, loading: false });
    }
  },
}));
