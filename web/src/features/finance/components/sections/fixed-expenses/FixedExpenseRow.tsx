import { CARD_ICONS, CATEGORIES, ICONS } from '../../../ui/constants';
import type { FixedExpense } from '../../../domain/types';
import { formatMoneyInput } from '../../../lib/moneyInput';
import { formatStartMonth } from '../../../lib/utils';
import { RowActions } from '../shared/RowActions';

interface FixedExpenseRowProps {
  item: FixedExpense;
  money: (value: number) => string;
  displayAmount: number;
  tempValue?: string;
  hasOverride: boolean;
  isPaid: boolean;
  onMonthAmountInput: (itemId: string, value: string) => void;
  onMonthAmountBlur: (item: Pick<FixedExpense, 'id' | 'amount'>) => void;
  onMonthAmountChange: (itemId: string, amount: string | null) => void;
  onTogglePaid: (itemId: string, paid: boolean) => void;
  onEdit: () => void;
  onDelete: () => void;
  cardIconMap?: Record<string, string>;
}

function resolvePaymentMethod(item: FixedExpense) {
  if (item.paymentMethod === 'cartao' && item.card) return item.card;
  return item.paymentMethod || 'boleto';
}

export function FixedExpenseRow({
  item,
  money,
  displayAmount,
  tempValue,
  hasOverride,
  isPaid,
  onMonthAmountInput,
  onMonthAmountBlur,
  onMonthAmountChange,
  onTogglePaid,
  onEdit,
  onDelete,
  cardIconMap,
}: FixedExpenseRowProps) {
  return (
    <tr>
      <td>{item.name}</td>
      <td>
        <div className="month-amount-cell">
          <input
            type="text"
            className={`month-amount-input ${hasOverride ? 'has-override' : ''}`}
            value={tempValue !== undefined ? tempValue : formatMoneyInput(displayAmount)}
            onChange={(e) => onMonthAmountInput(item.id, e.target.value)}
            onBlur={() => onMonthAmountBlur(item)}
            inputMode="numeric"
            autoComplete="off"
            placeholder={money(item.amount)}
          />
          {hasOverride && (
            <button
              type="button"
              className="clear-override"
              onClick={() => onMonthAmountChange(item.id, null)}
              title="Restaurar valor original"
            >
              ×
            </button>
          )}
        </div>
      </td>
      <td>
        {(() => {
          const methodOrCard = resolvePaymentMethod(item);
          if (item.paymentMethod === 'cartao' && item.card) {
            // Prefer dynamic card icon map, then CARD_ICONS, then ICONS
            return (
              (cardIconMap && cardIconMap[item.card]) ||
              CARD_ICONS[item.card as keyof typeof CARD_ICONS] ||
              ICONS[item.card] ||
              '💳'
            );
          }
          return ICONS[methodOrCard] || '💳';
        })()}
      </td>
      <td>{CATEGORIES[item.category] || '📦 OUTRO'}</td>
      <td>{item.dueDay ? `Dia ${item.dueDay}` : '-'}</td>
      <td>{formatStartMonth(item.startMonth)}</td>
      <td>
        <input
          type="checkbox"
          checked={isPaid}
          onChange={(e) => onTogglePaid(item.id, e.target.checked)}
          aria-label={`Marcar ${item.name} como pago no mês`}
        />
      </td>
      <td>
        <RowActions onEdit={onEdit} onDelete={onDelete} />
      </td>
    </tr>
  );
}
