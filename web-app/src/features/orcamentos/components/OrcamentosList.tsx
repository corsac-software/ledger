import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useOrcamentosStore } from '@/features/orcamentos/store';
import { useEffect, useState } from 'react';
import { Link } from '@tanstack/react-router';

export default function OrcamentosList() {
  const { orcamentos, loading, fetchOrcamentos, createOrcamento, deleteOrcamento } = useOrcamentosStore();
  const [showDialog, setShowDialog] = useState(false);
  const [anoMes, setAnoMes] = useState('');

  useEffect(() => {
    fetchOrcamentos();
  }, []);

  const handleCreate = () => {
    const normalized = anoMes.trim();
    if (!/^\d{6}$/.test(normalized)) return;
    createOrcamento({ idUsuario: 1, anoMes: normalized });
    setAnoMes('');
    setShowDialog(false);
  };

  const handleDelete = (id: number) => {
    if (confirm('Excluir este orçamento?')) {
      deleteOrcamento(id);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-semibold">Orçamentos Mensais</h2>
          <p className="text-muted-foreground">Gerencie seus orçamentos</p>
        </div>
        <Button onClick={() => setShowDialog(true)}>Novo Orçamento</Button>
      </div>

      {showDialog && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <Card className="w-full max-w-sm">
            <CardHeader>
              <CardTitle>Criar Orçamento</CardTitle>
              <CardDescription>Ano e mês (ex: 202602)</CardDescription>
            </CardHeader>
            <CardContent>
              <input
                type="text"
                value={anoMes}
                onChange={(e) => setAnoMes(e.target.value)}
                placeholder="202602"
                className="w-full border rounded-md px-3 py-2 mb-4"
              />
              <div className="flex gap-2 justify-end">
                <Button variant="outline" onClick={() => setShowDialog(false)}>Cancelar</Button>
                <Button onClick={handleCreate}>Criar</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {loading && <p className="text-muted-foreground">Carregando...</p>}

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {orcamentos.map((orc) => (
          <Card key={orc.id} className="hover:shadow-md transition-shadow">
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle className="text-lg">{orc.slug}</CardTitle>
                <Badge>{`${orc.ano}${String(orc.mes).padStart(2, '0')}`}</Badge>
              </div>
              <CardDescription>
                {orc.dataInicio} até {orc.dataFim}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex gap-2">
                <Link
                  to="/orcamentos/$id"
                  params={{ id: String(orc.id) }}
                  className="flex-1"
                >
                  <Button variant="outline" className="w-full">Ver Lançamentos</Button>
                </Link>
                <Button variant="destructive" size="sm" onClick={() => handleDelete(orc.id)}>Excluir</Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {!loading && orcamentos.length === 0 && (
        <p className="text-center text-muted-foreground mt-8">Nenhum orçamento encontrado</p>
      )}
    </div>
  );
}
