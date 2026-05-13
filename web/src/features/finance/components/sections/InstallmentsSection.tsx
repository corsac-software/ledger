import { useMemo } from 'react';
import { OVERRIDE_TYPES } from '../../domain/constants';
import type { CardBillItem, InstallmentItem } from '../../domain/types';
import { useCardList } from '../../hooks/useCardList';
import { useMonthPaymentMap } from '../../hooks/useMonthPaymentMap';
import { buildCardIconMap } from '../../lib/cardIconMap';
import { InstallmentForm, type InstallmentFormState } from './installments/InstallmentForm';
import { InstallmentRow } from './installments/InstallmentRow';
import { buildInstallmentPayload } from './installments/installmentFormHelpers';
import { INSTALLMENT_LABELS } from './installments/installmentSectionLabels';
import { useInstallmentCrudState } from './installments/useInstallmentCrudState';
import { CrudSection } from './shared/CrudSection';
import type { CrudSectionCommonProps, MonthPaidSectionProps } from './shared/types';

type InstallmentPayload = {
  name: string;
  installmentValue: number;
  totalInstallments: number;
  startMonth: string;
  card: string;
};

type InstallmentsSectionProps = CrudSectionCommonProps<InstallmentItem, InstallmentPayload> &
  MonthPaidSectionProps & {
    cardList?: CardBillItem[];
  };

export function InstallmentsSection({
  items,
  currentMonthKey,
  monthOverrides,
  onAdd,
  onEdit,
  onDelete,
  onTogglePaid,
  cardList,
}: InstallmentsSectionProps) {
  const cards = useCardList(cardList);
  const cardIconMap = useMemo(() => buildCardIconMap(cards, {}), [cards]);

  const { form, setForm, canSubmit, openCreateForm, openEditForm, resetForm } =
    useInstallmentCrudState({
      defaultCardId: cards[0]?.id || '',
      onDelete,
    });

  const monthPaymentMap = useMonthPaymentMap(
    monthOverrides,
    currentMonthKey,
    OVERRIDE_TYPES.INSTALLMENT_PAYMENT
  );

  const buildPayload = (currentForm: InstallmentFormState) => buildInstallmentPayload(currentForm);

  return (
    <CrudSection
      labels={INSTALLMENT_LABELS}
      items={items}
      form={form}
      canSubmit={canSubmit}
      resetForm={resetForm}
      openCreateForm={openCreateForm}
      openEditForm={openEditForm}
      buildPayload={buildPayload}
      onAdd={onAdd}
      onEdit={onEdit}
      onDelete={onDelete}
      renderForm={() => <InstallmentForm form={form} setForm={setForm} cards={cards} />}
      renderItem={(item, money, { openEdit, openDelete }) => (
        <InstallmentRow
          key={item.id}
          item={item}
          money={money}
          isPaid={monthPaymentMap.get(item.id)?.paid === true}
          cardIconMap={cardIconMap}
          onTogglePaid={onTogglePaid}
          onEdit={() => openEdit(item)}
          onDelete={() => openDelete(item)}
        />
      )}
    />
  );
}
