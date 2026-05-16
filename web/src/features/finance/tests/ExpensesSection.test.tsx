import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { ExpensesSection } from '../components/sections/ExpensesSection';

describe('ExpensesSection.tsx', () => {
  const defaultProps = {
    items: [],
    currentMonthKey: '2026-04',
    monthOverrides: [],
    onAdd: vi.fn(),
    onEdit: vi.fn(),
    onDelete: vi.fn(),
    onTogglePaid: vi.fn(),
  };

  it('presents fixed and variable expense modes', () => {
    render(<ExpensesSection {...defaultProps} />);

    expect(screen.getByRole('tab', { name: 'Fixas' })).toHaveAttribute('aria-selected', 'true');
    expect(screen.getByRole('tab', { name: 'Variaveis' })).toBeEnabled();
    expect(screen.getByText('DESPESAS FIXAS')).toBeInTheDocument();
  });

  it('opens the variable expenses placeholder tab', () => {
    render(<ExpensesSection {...defaultProps} />);

    fireEvent.click(screen.getByRole('tab', { name: 'Variaveis' }));

    expect(screen.getByRole('tab', { name: 'Variaveis' })).toHaveAttribute('aria-selected', 'true');
    expect(screen.getByText('DESPESAS VARIAVEIS')).toBeInTheDocument();
    expect(screen.getByText('Nova despesa variavel')).toBeDisabled();
  });
});
