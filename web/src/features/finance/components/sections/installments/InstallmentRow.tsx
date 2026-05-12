import type { InstallmentItem } from '../../../domain/types';
import { formatStartMonth } from '../../../lib/utils';
import { CARD_ICONS } from '../../../ui/constants';
import { RowActions } from '../shared/RowActions';

interface InstallmentRowProps {
  item: InstallmentItem;
  money: (value: number) => string;
  isPaid: boolean;
  cardIconMap?: Record<string, string>;
  onTogglePaid: (itemId: string, paid: boolean) => void;
  onEdit: () => void;
  onDelete: () => void;
}

export function InstallmentRow({
  item,
  money,
  isPaid,
  cardIconMap,
  onTogglePaid,
  onEdit,
  onDelete,
}: InstallmentRowProps) {
  const cardIcon = CARD_ICONS[item.card as keyof typeof CARD_ICONS];
  const icon = cardIcon || cardIconMap?.[item.card];
  const cardIconOnly = icon || '💳';

  return (
    <tr>
      <td>{item.name}</td>
      <td>{money(item.installmentValue)}</td>
      <td title={item.card}>{cardIconOnly}</td>
      <td>{formatStartMonth(item.startMonth)}</td>
      <td>
        <div
          className="progress-cell"
          data-progress={`${item.currentInstallment}/${item.totalInstallments}`}
        >
          <div className="progress-bar">
            <div
              className="progress-fill"
              data-progress={`${item.currentInstallment}/${item.totalInstallments}`}
              style={{
                width: `${(item.currentInstallment / item.totalInstallments) * 100}%`,
              }}
            />
          </div>
        </div>
      </td>
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
