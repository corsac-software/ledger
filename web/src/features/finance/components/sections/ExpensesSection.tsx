import type { CardBillItem, FixedExpense, MonthOverride } from '../../domain/types';
import { FixedExpensesSection } from './FixedExpensesSection';
import type { MonthPaidSectionProps } from './shared/types';
import { useState } from 'react';
import { Plus } from 'lucide-react';

type FixedExpensePayload = {
  name: string;
  amount: number;
  dueDay: number | null;
  startMonth: string;
  paymentMethod: string;
  card: string | null;
  category: string;
};

interface ExpensesSectionProps extends MonthPaidSectionProps {
  items: FixedExpense[];
  currentMonthKey: string;
  monthOverrides: MonthOverride[];
  cardList?: CardBillItem[];
  onAdd: (payload: FixedExpensePayload) => void | Promise<void>;
  onEdit: (id: string, payload: FixedExpensePayload) => void | Promise<void>;
  onDelete: (id: string) => void | Promise<void>;
  onMonthFixedExpenseAmount?: (itemId: string, amount: number | null) => void;
}

export function ExpensesSection(props: ExpensesSectionProps) {
  const [activeMode, setActiveMode] = useState<'fixed' | 'variable'>('fixed');

  return (
    <section className="expenses-section">
      <div className="expense-mode-tabs" role="tablist" aria-label="Tipos de despesas">
        <button
          type="button"
          className={`expense-mode-tab ${activeMode === 'fixed' ? 'active' : ''}`}
          role="tab"
          aria-selected={activeMode === 'fixed'}
          onClick={() => setActiveMode('fixed')}
        >
          Fixas
        </button>
        <button
          type="button"
          className={`expense-mode-tab ${activeMode === 'variable' ? 'active' : ''}`}
          role="tab"
          aria-selected={activeMode === 'variable'}
          onClick={() => setActiveMode('variable')}
        >
          Variaveis
        </button>
      </div>

      {activeMode === 'fixed' ? (
        <FixedExpensesSection {...props} />
      ) : (
        <section className="section expense-content-section variable-expenses-section">
          <div className="sec-header section-header--finflow">
            <div>
              <p className="sec-title">DESPESAS VARIAVEIS</p>
              <p className="sec-description">
                Lancamentos avulsos do mes entram no proximo roadmap.
              </p>
            </div>
            <div className="sec-actions">
              <button type="button" className="add-btn add-btn--primary" disabled>
                <Plus size={13} strokeWidth={2.4} aria-hidden />
                Nova despesa variavel
              </button>
            </div>
          </div>
          <div className="rule-empty-state variable-expenses-empty">
            <p>Nenhuma despesa variavel lancada ainda.</p>
            <span>
              O cadastro rapido foi movido para o roadmap de gastos variaveis e faturas.
            </span>
          </div>
        </section>
      )}
    </section>
  );
}
