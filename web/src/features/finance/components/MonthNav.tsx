import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from 'react';
import type { CardBillItem } from '../domain/types';
import { useCardList } from '../hooks/useCardList';
import { detectBankColor } from '../lib/bankColors';
import { useI18n } from '../lib/i18n';
import { applyMoneyMask, formatMoneyInput, parseMoneyInput } from '../lib/moneyInput';
import { EmptyBillsState } from './EmptyBillsState';
import { ConfirmModal, RuleModal } from './modals';

interface MonthNavProps {
  label: string;
  onPrev: () => void;
  onNext: () => void;
  theme: 'default' | 'premium';
  onToggleTheme: () => void;
  cardBills: Record<string, number>;
  onSetCardBill: (cardId: string, amount: number | null) => void;
  cardList?: CardBillItem[];
  onSetCardList?: (list: CardBillItem[]) => void;
  cardDeleteReasons?: Record<string, string>;
}

function BillCard({
  card,
  displayName,
  value,
  onChange,
  onDelete,
  _canDelete,
  deleteReason,
}: {
  card: CardBillItem;
  displayName: string;
  value: string;
  onChange: (value: string) => void;
  onDelete: () => void;
  _canDelete: boolean;
  deleteReason?: string;
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [inputValue, setInputValue] = useState(value);
  const [hasValue, setHasValue] = useState(false);
  const inputShellRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const hasCommittedRef = useRef(false);
  const displayValue = value.replace(/^R\$\s*/, '');

  useEffect(() => {
    if (!isEditing) {
      setInputValue(value);
    }
    setHasValue(!!value && value !== 'R$ 0,00');
  }, [isEditing, value]);

  useLayoutEffect(() => {
    if (!isEditing) return;

    hasCommittedRef.current = false;
    inputRef.current?.focus();
    const frameId = requestAnimationFrame(() => {
      inputRef.current?.focus();
      inputRef.current?.select();
    });
    const timeoutId = window.setTimeout(() => {
      inputRef.current?.focus();
      inputRef.current?.select();
    }, 0);

    return () => {
      cancelAnimationFrame(frameId);
      window.clearTimeout(timeoutId);
    };
  }, [isEditing]);

  const commitInputValue = useCallback(() => {
    if (hasCommittedRef.current) return;
    hasCommittedRef.current = true;
    setIsEditing(false);
    const parsed = parseMoneyInput(inputValue, { allowZero: true });
    if (parsed !== null && parsed > 0) {
      const formatted = formatMoneyInput(parsed);
      onChange(formatted);
      setInputValue(formatted);
      setHasValue(true);
    } else {
      onChange('');
      setInputValue('');
      setHasValue(false);
    }
  }, [inputValue, onChange]);

  useEffect(() => {
    if (!isEditing) return;

    const handlePointerDown = (event: PointerEvent) => {
      if (inputShellRef.current?.contains(event.target as Node)) return;
      inputRef.current?.blur();
      commitInputValue();
    };

    document.addEventListener('pointerdown', handlePointerDown);
    return () => document.removeEventListener('pointerdown', handlePointerDown);
  }, [commitInputValue, isEditing]);

  const handleBlur = () => {
    commitInputValue();
  };

  const startEditing = () => {
    setIsEditing(true);
    window.setTimeout(() => {
      inputRef.current?.focus();
      const length = inputRef.current?.value.length || 0;
      inputRef.current?.setSelectionRange(length, length);
    }, 100);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') inputRef.current?.blur();
    if (e.key === 'Escape') {
      setInputValue(value);
      setIsEditing(false);
    }
  };

  const getCardStyle = (): React.CSSProperties => {
    if (card.color) {
      const style: React.CSSProperties & Record<string, string | undefined> = {
        border: `1px solid color-mix(in srgb, ${card.color} 75%, #0b2b57 20%)`,
      };
      style['--bill-card-color'] = card.color;
      return style;
    }
    return {};
  };

  return (
    <div className="bill-card" style={getCardStyle()}>
      <div className="bill-card-top">
        <span className="bill-card-name" title={displayName}>
          {displayName}
        </span>
        {deleteReason ? (
          <span className="bill-card-status">EM USO</span>
        ) : (
          <button
            type="button"
            className="bill-card-delete"
            onClick={(e) => {
              e.stopPropagation();
              onDelete();
            }}
            aria-label={`Apagar cartão ${displayName}`}
            title={`Apagar cartão ${displayName}`}
          >
            Excluir
          </button>
        )}
      </div>

      <div className="bill-card-divider" />

      <div className="bill-card-bottom">
        <span className="bill-card-label">FATURA</span>
      </div>

      <div className={`bill-display${isEditing ? ' editing' : ''}`}>
        <span className="bill-currency">R$</span>
        <p
          className="bill-card-value"
          onPointerDown={(e) => {
            if (hasValue) e.preventDefault();
          }}
          onClick={(e) => {
            e.stopPropagation();
            if (!isEditing && hasValue) startEditing();
          }}
          style={{ cursor: hasValue && !isEditing ? 'pointer' : 'default' }}
        >
          {displayValue}
        </p>
        {!hasValue && !isEditing && (
          <button
            type="button"
            className="bill-card-add-value"
            onPointerDown={(e) => {
              e.preventDefault();
              e.stopPropagation();
            }}
            onClick={(e) => {
              e.stopPropagation();
              startEditing();
            }}
          >
            + Incluir fatura
          </button>
        )}
        <div
          ref={inputShellRef}
          className={`bill-input-shell${isEditing ? ' visible' : ''}`}
          onMouseDown={(e) => e.stopPropagation()}
          onClick={(e) => e.stopPropagation()}
        >
          <input
            ref={inputRef}
            className="bill-card-input"
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(applyMoneyMask(e.target.value))}
            onBlur={handleBlur}
            onKeyDown={handleKeyDown}
            inputMode="decimal"
            autoComplete="off"
            aria-label={`Valor da fatura ${displayName}`}
            placeholder="0,00"
          />
        </div>
      </div>
    </div>
  );
}

export default function MonthNav({
  label,
  onPrev,
  onNext,
  theme,
  onToggleTheme,
  cardBills,
  onSetCardBill,
  cardList,
  onSetCardList,
  cardDeleteReasons,
}: MonthNavProps) {
  const { normalizeCardName } = useI18n();
  const isDarkTheme = theme === 'premium';
  const nextThemeIcon = isDarkTheme ? '☀' : '🌙';
  const nextThemeLabel = isDarkTheme ? 'Claro' : 'Escuro';
  const cards = useCardList(cardList);
  const hasCards = cards.length > 0;

  // Memoize the computation of billInputs to avoid redundant calculations
  const computedBillInputs = useMemo(
    () =>
      cards.reduce(
        (acc, c) => {
          acc[c.id] = formatMoneyInput(cardBills?.[c.id], { hideNonPositive: true });
          return acc;
        },
        {} as Record<string, string>
      ),
    [cardBills, cards]
  );

  const [billInputs, setBillInputs] = useState(computedBillInputs);

  // Keep local state in sync with computed values
  useEffect(() => {
    setBillInputs(computedBillInputs);
  }, [computedBillInputs]);

  const handleBillInputChange = (cardId: string, rawValue: string) => {
    const masked = applyMoneyMask(rawValue);
    setBillInputs((prev) => ({ ...prev, [cardId]: masked }));
    onSetCardBill(cardId, parseMoneyInput(masked, { allowZero: false }));
  };

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [newCardName, setNewCardName] = useState('');
  const [newCardIcon, setNewCardIcon] = useState('💳');
  const [deleteTarget, setDeleteTarget] = useState<CardBillItem | null>(null);
  const [canScrollIconSelector, setCanScrollIconSelector] = useState(false);
  const iconSelectorRef = useRef<HTMLDivElement>(null);

  const iconOptions = [
    '💳',
    '🔴',
    '🟠',
    '🟡',
    '🟢',
    '🔵',
    '🟣',
    '⚪',
    '⚫',
    '🟤',
    '🩷',
    '🩵',
    '🩶',
    '❤️',
    '💛',
    '💚',
    '💙',
    '💜',
    '🖤',
    '🏦',
    '🏠',
    '💰',
    '🪙',
  ];

  const resetAddCardForm = () => {
    setNewCardName('');
    setNewCardIcon('💳');
  };

  const openAddCardModal = () => {
    resetAddCardForm();
    setIsAddModalOpen(true);
  };

  const closeAddCardModal = () => {
    setIsAddModalOpen(false);
    resetAddCardForm();
  };

  const updateIconSelectorScrollState = () => {
    const element = iconSelectorRef.current;
    if (!element) {
      setCanScrollIconSelector(false);
      return;
    }

    const hasOverflow = element.scrollWidth > element.clientWidth + 1;
    const isAtEnd = element.scrollLeft + element.clientWidth >= element.scrollWidth - 1;
    setCanScrollIconSelector(hasOverflow && !isAtEnd);
  };

  const handleIconSelect = (icon: string, event: React.MouseEvent<HTMLButtonElement>) => {
    setNewCardIcon(icon);
    event.currentTarget.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'nearest' });
  };

  const handleAddCard = () => {
    if (!onSetCardList) return;
    const id = newCardName.trim().toLowerCase().replace(/\s+/g, '-');
    if (!id) return;

    const cardColor = detectBankColor(newCardName);

    const newCard: CardBillItem = {
      id,
      name: newCardName.trim(),
      icon: newCardIcon,
    };
    if (cardColor) {
      newCard.color = cardColor;
    }
    const next = [...(cardList || []), newCard];
    onSetCardList(next);
    closeAddCardModal();
  };

  const handleDeleteCard = (cardId: string) => {
    if (!onSetCardList) return;
    const target = cards.find((card) => card.id === cardId);
    if (!target) return;
    setDeleteTarget(target);
  };

  useEffect(() => {
    if (!hasCards) {
      setIsAddModalOpen(false);
      setNewCardName('');
      setNewCardIcon('💳');
    }
  }, [hasCards]);

  useEffect(() => {
    if (!isAddModalOpen) {
      setCanScrollIconSelector(false);
      return;
    }

    const frame = window.requestAnimationFrame(updateIconSelectorScrollState);
    window.addEventListener('resize', updateIconSelectorScrollState);

    return () => {
      window.cancelAnimationFrame(frame);
      window.removeEventListener('resize', updateIconSelectorScrollState);
    };
  }, [isAddModalOpen]);

  return (
    <div className="month-nav">
      <div className="month-nav-top">
        <div className="month-stepper" role="group" aria-label="Navegacao de meses">
          <button
            className="month-step-btn month-step-btn--icon"
            type="button"
            onClick={onPrev}
            aria-label="Mes anterior"
          >
            ←
          </button>
          <h2>{label}</h2>
          <button
            className="month-step-btn month-step-btn--icon"
            type="button"
            onClick={onNext}
            aria-label="Proximo mes"
          >
            →
          </button>
        </div>
        <button
          className="theme-btn"
          onClick={onToggleTheme}
          aria-label={`Mudar para tema ${nextThemeLabel}`}
          title={`Mudar para tema ${nextThemeLabel}`}
        >
          <span aria-hidden="true" className="theme-btn-icon">
            {nextThemeIcon}
          </span>
        </button>
      </div>
      <div className="card-bill-panel">
        <div className="card-bill-panel-head">
          <p className="card-bill-title">Faturas do mês</p>
          {hasCards && onSetCardList ? (
            <button type="button" className="card-bill-add-btn" onClick={openAddCardModal}>
              + Novo cartão
            </button>
          ) : null}
        </div>

        {hasCards ? (
          <div className="card-bill-grid">
            {cards.map((c) => (
              <BillCard
                key={c.id}
                card={c}
                displayName={normalizeCardName(c.name)}
                value={billInputs?.[c.id] || ''}
                onChange={(v) => handleBillInputChange(c.id, v)}
                onDelete={() => handleDeleteCard(c.id)}
                _canDelete={!cardDeleteReasons?.[c.id]}
                deleteReason={cardDeleteReasons?.[c.id]}
              />
            ))}
          </div>
        ) : (
          <EmptyBillsState canAdd={!!onSetCardList} onAddCard={openAddCardModal} />
        )}
      </div>

      <RuleModal
        open={isAddModalOpen}
        title="Adicionar cartão"
        submitLabel="Adicionar"
        onClose={closeAddCardModal}
        onSubmit={(event) => {
          event.preventDefault();
          handleAddCard();
        }}
      >
        <div className="card-bill-add-form">
          <input
            className="card-bill-add-input"
            type="text"
            placeholder="Nome do cartão"
            value={newCardName}
            onChange={(e) => setNewCardName(e.target.value)}
          />
          <div className="icon-selector-shell">
            <div
              ref={iconSelectorRef}
              className={`icon-selector ${canScrollIconSelector ? 'icon-selector--glow' : ''}`}
              onScroll={updateIconSelectorScrollState}
            >
              {iconOptions.map((icon) => (
                <button
                  key={icon}
                  type="button"
                  className={`icon-option ${newCardIcon === icon ? 'selected' : ''}`}
                  onClick={(event) => handleIconSelect(icon, event)}
                >
                  {icon}
                </button>
              ))}
            </div>
          </div>
          {newCardName && detectBankColor(newCardName) ? (
            <div className="color-detection-info">
              <p className="color-detection-label">
                🎨 Cor fixa detectada automaticamente para <strong>{newCardName}</strong>
              </p>
              <div
                className="color-preview"
                style={{
                  background: `color-mix(in srgb, ${detectBankColor(newCardName)} 18%, transparent)`,
                  border: `2px solid ${detectBankColor(newCardName)}`,
                }}
                title="Cor do cartão"
              >
                <span style={{ fontSize: '20px' }}>{newCardIcon}</span>
              </div>
            </div>
          ) : null}
        </div>
      </RuleModal>

      <ConfirmModal
        open={!!deleteTarget}
        title="Apagar cartão"
        message={`Tem certeza que deseja apagar o cartão "${deleteTarget?.name || ''}"?`}
        confirmLabel="Apagar"
        onConfirm={() => {
          if (!deleteTarget || !onSetCardList) return;
          onSetCardList(cards.filter((card) => card.id !== deleteTarget.id));
          onSetCardBill(deleteTarget.id, null);
          setDeleteTarget(null);
        }}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
