import { createFinanceId } from '../lib/ids';
import type { FixedExpense, Installment, Revenue } from './types';

export function createDefaultFixedExpense(data: Partial<FixedExpense>): FixedExpense {
  return {
    id: createFinanceId('fixed'),
    active: true,
    notes: '',
    endMonth: null,
    ...data,
  } as FixedExpense;
}

export function createDefaultRevenue(data: Partial<Revenue>): Revenue {
  return {
    id: createFinanceId('rev'),
    active: true,
    notes: '',
    endMonth: null,
    ...data,
  } as Revenue;
}

export function createDefaultInstallment(data: Partial<Installment>): Installment {
  return {
    id: createFinanceId('inst'),
    active: true,
    closedAt: null,
    currentInstallment: 1,
    ...data,
  } as Installment;
}
