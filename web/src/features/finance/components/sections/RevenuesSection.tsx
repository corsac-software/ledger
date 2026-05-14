import type { Revenue } from '../../domain/types';
import { useActiveRevenues } from '../../hooks/useActiveRevenues';
import { formatMoney } from '../../lib/utils';
import { RevenueForm, type RevenueFormState } from './revenues/RevenueForm';
import { RevenueRow } from './revenues/RevenueRow';
import {
  buildRevenuePayload,
  toRevenueCreateItem,
  toRevenueEditItem,
} from './revenues/revenueFormHelpers';
import { REVENUE_LABELS } from './revenues/revenueSectionLabels';
import { useRevenueCrudState } from './revenues/useRevenueCrudState';
import { CrudSection } from './shared/CrudSection';
import type { CrudSectionCommonProps } from './shared/types';
import { useRevenueMonthAmountInput } from './shared/useRevenueMonthAmountInput';

type RevenuePayload = { name: string; amount: number; startMonth: string };
type RevenuesSectionProps = CrudSectionCommonProps<Revenue, RevenuePayload> & {
  currentMonthKey: string;
  monthRevenueAmounts: Record<string, number>;
  onMonthRevenueAmount?: (itemId: string, amount: number | null) => void;
};

export function RevenuesSection({
  items,
  currentMonthKey,
  monthRevenueAmounts,
  onAdd,
  onEdit,
  onDelete,
  onMonthRevenueAmount,
}: RevenuesSectionProps) {
  const { form, setForm, canSubmit, openCreateForm, openEditForm, resetForm } = useRevenueCrudState(
    {
      currentMonthKey,
      onDelete,
    }
  );

  const activeItems = useActiveRevenues(items, currentMonthKey);
  const totalRevenue = activeItems.reduce((sum, item) => {
    const amount =
      monthRevenueAmounts && monthRevenueAmounts[item.id] !== undefined
        ? monthRevenueAmounts[item.id]
        : item.baseAmount;
    return sum + Number(amount || 0);
  }, 0);
  const averageRevenue = activeItems.length ? totalRevenue / activeItems.length : 0;

  const buildPayload = (currentForm: RevenueFormState) => buildRevenuePayload(currentForm);

  const {
    tempInputValues,
    handleMonthAmountChange,
    handleMonthAmountInput,
    handleMonthAmountBlur,
  } = useRevenueMonthAmountInput(onMonthRevenueAmount);

  return (
    <CrudSection
      labels={REVENUE_LABELS}
      items={activeItems}
      form={form}
      canSubmit={canSubmit}
      resetForm={resetForm}
      openCreateForm={openCreateForm}
      openEditForm={openEditForm}
      buildPayload={buildPayload}
      onAdd={(payload) => onAdd(toRevenueCreateItem(payload!))}
      onEdit={(id, payload) => onEdit(id, toRevenueEditItem(payload!))}
      onDelete={onDelete}
      topContent={
        <section className="revenue-summary-row" aria-label="Resumo de receitas">
          <div className="mcard">
            <p className="mcard-label">TOTAL DO MES</p>
            <p className="mcard-val pos">{formatMoney(totalRevenue)}</p>
          </div>
          <div className="mcard">
            <p className="mcard-label">LANCAMENTOS</p>
            <p className="mcard-val">{activeItems.length}</p>
          </div>
          <div className="mcard">
            <p className="mcard-label">MEDIA</p>
            <p className="mcard-val">{formatMoney(averageRevenue)}</p>
          </div>
        </section>
      }
      renderForm={() => <RevenueForm form={form} setForm={setForm} />}
      renderItem={(item, money, { openEdit, openDelete }) => {
        const hasOverride = monthRevenueAmounts && monthRevenueAmounts[item.id] !== undefined;

        return (
          <RevenueRow
            key={item.id}
            item={item}
            money={money}
            displayAmount={hasOverride ? monthRevenueAmounts[item.id] : item.baseAmount}
            tempValue={tempInputValues[item.id]}
            hasOverride={hasOverride}
            onMonthAmountInput={handleMonthAmountInput}
            onMonthAmountBlur={handleMonthAmountBlur}
            onMonthAmountChange={handleMonthAmountChange}
            onEdit={() => openEdit(item)}
            onDelete={() => openDelete(item)}
          />
        );
      }}
    />
  );
}
