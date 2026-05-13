import { useMemo } from 'react';
import { useFinance } from '../context/FinanceContext';
import {
  createDefaultFixedExpense,
  createDefaultInstallment,
  createDefaultRevenue,
} from '../domain/factories';
import type { FixedExpense, Installment, Revenue } from '../domain/types';

export function useFinanceActions() {
  const {
    addFixedExpense: _addFixedExpense,
    addRevenue: _addRevenue,
    addInstallment: _addInstallment,
    updateFixedExpense: _updateFixedExpense,
    updateRevenue: _updateRevenue,
    updateInstallment: _updateInstallment,
    removeFixedExpense: _removeFixedExpense,
    removeRevenue: _removeRevenue,
    removeInstallment: _removeInstallment,
    upsertMonthOverride: _upsertMonthOverride,
    clearMonthOverride: _clearMonthOverride,
    changeMonth: _changeMonth,
    resetDatabase: _resetDatabase,
    importFinanceState: _importFinanceState,
    setTheme: _setTheme,
    setCardBills: _setCardBills,
  } = useFinance();

  const actions = useMemo(
    () => ({
      addFixedExpense: (data: Partial<FixedExpense>) =>
        _addFixedExpense(createDefaultFixedExpense(data)),
      addRevenue: (data: Partial<Revenue>) => _addRevenue(createDefaultRevenue(data)),
      addInstallment: (data: Partial<Installment>) =>
        _addInstallment(createDefaultInstallment(data)),
      updateFixedExpense: _updateFixedExpense,
      updateRevenue: _updateRevenue,
      updateInstallment: _updateInstallment,
      removeFixedExpense: _removeFixedExpense,
      removeRevenue: _removeRevenue,
      removeInstallment: _removeInstallment,
      upsertMonthOverride: _upsertMonthOverride,
      clearMonthOverride: _clearMonthOverride,
      changeMonth: _changeMonth,
      resetDatabase: _resetDatabase,
      importFinanceState: _importFinanceState,
      setTheme: _setTheme,
      setCardBills: _setCardBills,
    }),
    [
      _addFixedExpense,
      _addRevenue,
      _addInstallment,
      _updateFixedExpense,
      _updateRevenue,
      _updateInstallment,
      _removeFixedExpense,
      _removeRevenue,
      _removeInstallment,
      _upsertMonthOverride,
      _clearMonthOverride,
      _changeMonth,
      _resetDatabase,
      _importFinanceState,
      _setTheme,
      _setCardBills,
    ]
  );

  return actions;
}
