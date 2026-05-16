import { createFinanceId } from '../lib/ids';
import { ALLOWED_PAYMENT_METHODS, DEFAULT_CARD_ID } from './constants';
import type { FixedExpense, Installment, Revenue } from './types';

export function normalizeFixedExpense(item: Record<string, unknown>): FixedExpense {
  const paymentMethod = item?.paymentMethod as string | undefined;
  const card = item?.card as string | undefined;

  if (paymentMethod === 'cartao') {
    const normalizedCard = card?.trim() === '' ? DEFAULT_CARD_ID : card || DEFAULT_CARD_ID;
    return { ...item, paymentMethod: 'cartao', card: normalizedCard } as unknown as FixedExpense;
  }

  if (
    !ALLOWED_PAYMENT_METHODS.includes(paymentMethod as (typeof ALLOWED_PAYMENT_METHODS)[number])
  ) {
    return { ...item, paymentMethod: 'boleto', card: null } as unknown as FixedExpense;
  }

  // For non-cartao valid payment methods, remove card information
  return { ...item, card: null } as unknown as FixedExpense;
}

export function normalizeInstallment(item: Record<string, unknown>): Installment {
  const raw = item?.card;
  const trimmed =
    typeof raw === 'string'
      ? raw.trim()
      : typeof raw === 'number' && Number.isFinite(raw)
        ? String(raw)
        : '';
  const normalizedCard = trimmed !== '' ? trimmed : DEFAULT_CARD_ID;
  return { ...item, card: normalizedCard } as unknown as Installment;
}

export function normalizeRevenue(item: Record<string, unknown>): Revenue {
  const amount = item?.amount as number | undefined;
  const baseAmount = item?.baseAmount as number | undefined;
  const id = item?.id as string | undefined;

  const updates: Record<string, unknown> = {};
  const rest = { ...item };
  delete rest.category;

  if (amount !== undefined && baseAmount === undefined) {
    updates.baseAmount = amount;
  }

  if (!id || id.trim() === '') {
    updates.id = createFinanceId('rev');
  }

  if (item.recurring === undefined) {
    updates.recurring = true;
  }

  if (item.paymentDay === undefined) {
    updates.paymentDay = null;
  }

  if (Object.keys(updates).length > 0) {
    return { ...rest, ...updates } as unknown as Revenue;
  }

  return rest as unknown as Revenue;
}
