import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useOrcamentosStore } from '@/features/orcamentos/store';
import { useEffect, useState } from 'react';
import { Link, useParams } from '@tanstack/react-router';
import type { CreateLancamentoRequest } from '@/features/orcamentos/types';

export default function OrcamentoDetail() {
  const { id } = useParams({ strict: false });
  const orcamentoId = Number(id);
  const { selectedOrcamento, lancamentos, loading, fetchOrcamento, fetchLancamentos, createLancamento, deleteLancamento } = useOrcamentosStore();
  const [showDialog, setShowDialog] = useState(false);
  const [form, setForm] = useState<CreateLancamentoRequest>({
    descricao: '',
    valor: 0,
    tipo: 'RECEITA',
  });

  useEffect(() => {
    fetchOrcamento(orcamentoId);
    fetchLancamentos(orcamentoId);
  }, [orcamentoId]);

  const handleCreate = () => {
    if (!form.descricao || form.valor <= 0) return;
    const payload: CreateLancamentoRequest = {
      ...form,
      statusDespesa: form.tipo === 'DESPESA' ? (form.statusDespesa ?? 'ABERTO') : undefined,
    };
    createLancamento(orcamentoId, payload);
    setForm({ descricao: '', valor: 0, tipo: 'RECEITA' });
    setShowDialog(false);
  };

  const handleDelete = (lancamentoId: number) => {
    if (confirm('Excluir este lançamento?')) {
      deleteLancamento(orcamentoId, lancamentoId);
    }
  };

  const receitas = lancamentos.filter((l) => l.tipo === 'RECEITA');
  const despesas = lancamentos.filter((l) => l.tipo === 'DESPESA');

  const totalReceitas = receitas.reduce((s, l) => s + l.valor, 0);
  const totalDespesas = despesas.reduce((s, l) => s + l.valor, 0);
  const saldo = totalReceitas - totalDespesas;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <Link to="/orcamentos" className="text-sm text-muted-foreground hover:underline">
            ← Voltar
          </Link>
          <h2 className="text-2xl font-semibold mt-1">
            Orçamento {selectedOrcamento?.slug || '...'}
          </h2>
        </div>
        <Button onClick={() => setShowDialog(true)}>Novo Lançamento</Button>
      </div>

      {showDialog && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-sm">
            <CardHeader>
              <CardTitle>Criar Lançamento</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                <input
                  type="text"
                  value={form.descricao}
                  onChange={(e) => setForm({ ...form, descricao: e.target.value })}
                  placeholder="Descrição"
                  className="w-full border rounded-md px-3 py-2"
                />
                <input
                  type="number"
                  value={form.valor || ''}
                  onChange={(e) => setForm({ ...form, valor: Number(e.target.value) })}
                  placeholder="Valor"
                  className="w-full border rounded-md px-3 py-2"
                />
                <select
                  value={form.tipo}
                  onChange={(e) => {
                    const tipo = e.target.value as 'RECEITA' | 'DESPESA';
                    setForm({
                      ...form,
                      tipo,
                      statusDespesa: tipo === 'DESPESA' ? (form.statusDespesa ?? 'ABERTO') : undefined,
                    });
                  }}
                  className="w-full border rounded-md px-3 py-2"
                >
                  <option value="RECEITA">Receita</option>
                  <option value="DESPESA">Despesa</option>
                </select>
                {form.tipo === 'DESPESA' && (
                  <select
                    value={form.statusDespesa ?? 'ABERTO'}
                    onChange={(e) =>
                      setForm({
                        ...form,
                        statusDespesa: e.target.value as 'ABERTO' | 'RESERVADO' | 'PAGA',
                      })
                    }
                    className="w-full border rounded-md px-3 py-2"
                  >
                    <option value="ABERTO">Aberto</option>
                    <option value="RESERVADO">Reservado</option>
                    <option value="PAGA">Paga</option>
                  </select>
                )}
                <div className="flex gap-2 justify-end">
                  <Button variant="outline" onClick={() => setShowDialog(false)}>Cancelar</Button>
                  <Button onClick={handleCreate}>Criar</Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      <div className="grid gap-4 md:grid-cols-3 mb-6">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-muted-foreground">Receitas</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-2xl font-bold text-green-600">R$ {totalReceitas.toFixed(2)}</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-muted-foreground">Despesas</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-2xl font-bold text-red-600">R$ {totalDespesas.toFixed(2)}</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-muted-foreground">Saldo</CardTitle>
          </CardHeader>
          <CardContent>
            <p className={`text-2xl font-bold ${saldo >= 0 ? 'text-green-600' : 'text-red-600'}`}>
              R$ {saldo.toFixed(2)}
            </p>
          </CardContent>
        </Card>
      </div>

      {loading && <p className="text-muted-foreground">Carregando...</p>}

      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Receitas ({receitas.length})</CardTitle>
          </CardHeader>
          <CardContent>
            {receitas.map((l) => (
              <div key={l.id} className="flex items-center justify-between py-2 border-b last:border-0">
                <div>
                  <p className="font-medium">{l.descricao}</p>
                  <p className="text-xs text-muted-foreground">{l.slug}</p>
                </div>
                <div className="flex items-center gap-2">
                  <span className="font-semibold text-green-600">R$ {l.valor.toFixed(2)}</span>
                  <Button variant="ghost" size="sm" onClick={() => handleDelete(l.id)}>×</Button>
                </div>
              </div>
            ))}
            {receitas.length === 0 && <p className="text-muted-foreground text-sm">Nenhuma receita</p>}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Despesas ({despesas.length})</CardTitle>
          </CardHeader>
          <CardContent>
            {despesas.map((l) => (
              <div key={l.id} className="flex items-center justify-between py-2 border-b last:border-0">
                <div>
                  <p className="font-medium">{l.descricao}</p>
                  <p className="text-xs text-muted-foreground">{l.slug}</p>
                </div>
                <div className="flex items-center gap-2">
                  <Badge variant={l.statusDespesa === 'PAGA' ? 'default' : l.statusDespesa === 'RESERVADO' ? 'secondary' : 'outline'}>
                    {l.statusDespesa || 'ABERTO'}
                  </Badge>
                  <span className="font-semibold text-red-600">R$ {l.valor.toFixed(2)}</span>
                  <Button variant="ghost" size="sm" onClick={() => handleDelete(l.id)}>×</Button>
                </div>
              </div>
            ))}
            {despesas.length === 0 && <p className="text-muted-foreground text-sm">Nenhuma despesa</p>}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
