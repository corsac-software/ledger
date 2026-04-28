export type TipoLancamento = 'RECEITA' | 'DESPESA';
export type StatusDespesa = 'ABERTO' | 'RESERVADO' | 'PAGA';

export interface OrcamentoMensal {
  id: number;
  idUsuario: number;
  ano: number;
  mes: number;
  slug: string;
  dataInicio: string;
  dataFim: string;
  seqReceita: number;
  seqDespesa: number;
}

export interface LancamentoMensal {
  id: number;
  slug: string;
  descricao: string;
  valor: number;
  tipo: TipoLancamento;
  statusDespesa?: StatusDespesa;
}

export interface CreateOrcamentoRequest {
  idUsuario: number;
  anoMes: string;
}

export interface CreateLancamentoRequest {
  descricao: string;
  valor: number;
  tipo: TipoLancamento;
  statusDespesa?: StatusDespesa;
}

export interface UpdateLancamentoRequest {
  descricao?: string;
  valor?: number;
  statusDespesa?: StatusDespesa;
}
