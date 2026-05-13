import type { Revenue } from '../../../domain/types';
import { formatMoneyInput, parseMoneyInput } from '../../../lib/moneyInput';
import { createFormHelpers } from '../shared/createFormHelpers';
import type { RevenueFormState } from './RevenueForm';

type RevenuePayload = { name: string; amount: number; startMonth: string };

const revenueFormHelpers = createFormHelpers<
  RevenueFormState,
  Revenue,
  RevenuePayload,
  [currentMonthKey: string],
  [currentMonthKey: string]
>({
  createEmptyForm: (currentMonthKey) => ({ name: '', amount: '', startMonth: currentMonthKey }),
  createEditForm: (item, currentMonthKey) => ({
    name: item.name || '',
    amount: formatMoneyInput(item.baseAmount),
    startMonth: item.startMonth || currentMonthKey,
  }),
  buildPayload: (currentForm) => {
    const amount = parseMoneyInput(currentForm.amount);
    if (amount === null) return null;

    return {
      name: currentForm.name,
      amount,
      startMonth: currentForm.startMonth,
    };
  },
});

export const createRevenueEmptyForm = revenueFormHelpers.createEmptyForm;
export const createRevenueEditForm = revenueFormHelpers.createEditForm;
export const buildRevenuePayload = revenueFormHelpers.buildPayload;

export function toRevenueCreateItem(payload: RevenuePayload) {
  return {
    ...payload,
    baseAmount: payload.amount,
    active: true,
    endMonth: null,
    category: 'outro',
    notes: '',
  };
}

export function toRevenueEditItem(payload: RevenuePayload) {
  return {
    ...payload,
    baseAmount: payload.amount,
  };
}
