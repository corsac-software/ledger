import { ALLOWED_PAYMENT_METHODS, DEFAULT_CARD_ID } from '../../../domain/constants';
import type { CardBillItem, FixedExpense } from '../../../domain/types';
import { parseMoneyInput } from '../../../lib/moneyInput';
import { resolvePaymentMethod } from '../../../lib/utils';
import type { FixedExpenseFormState } from './FixedExpenseForm';

function isSpecialPaymentMethod(value: string) {
  return ALLOWED_PAYMENT_METHODS.includes(value as (typeof ALLOWED_PAYMENT_METHODS)[number]);
}

export function createFixedExpenseEmptyForm(
  currentMonthKey: string,
  defaultCardId = ''
): FixedExpenseFormState {
  return {
    name: '',
    amount: '',
    dueDay: '',
    startMonth: currentMonthKey,
    paymentMethod: defaultCardId || 'boleto',
    card: defaultCardId,
    category: 'outro',
  };
}

export function createFixedExpenseEditForm(
  item: FixedExpense,
  currentMonthKey: string
): FixedExpenseFormState {
  return {
    name: item.name || '',
    amount: item.amount.toFixed(2).replace('.', ','),
    dueDay: item.dueDay ? String(item.dueDay) : '',
    startMonth: item.startMonth || currentMonthKey,
    paymentMethod:
      item.paymentMethod === 'cartao' ? item.card || 'cartao' : resolvePaymentMethod(item),
    card: item.card || '',
    category: item.category || 'outro',
  };
}

export function buildFixedExpensePayload(
  currentForm: FixedExpenseFormState,
  cards: CardBillItem[]
): {
  name: string;
  amount: number;
  dueDay: number | null;
  startMonth: string;
  paymentMethod: string;
  card: string | null;
  category: string;
} | null {
  const amount = parseMoneyInput(currentForm.amount);
  if (amount === null) return null;

  const selectedCard = cards.find((card) => card.id === currentForm.paymentMethod);
  const legacyCard = !isSpecialPaymentMethod(currentForm.paymentMethod)
    ? currentForm.paymentMethod
    : null;

  return {
    name: currentForm.name,
    amount,
    dueDay: currentForm.dueDay ? Number(currentForm.dueDay) : null,
    startMonth: currentForm.startMonth,
    paymentMethod: selectedCard || legacyCard ? 'cartao' : currentForm.paymentMethod,
    card: selectedCard
      ? selectedCard.id
      : legacyCard
        ? legacyCard
        : currentForm.paymentMethod === 'cartao'
          ? currentForm.card || cards[0]?.id || DEFAULT_CARD_ID
          : null,
    category: currentForm.category,
  };
}
