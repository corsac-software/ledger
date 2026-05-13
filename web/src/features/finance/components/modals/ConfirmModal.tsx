import { ModalShell } from './ModalShell';

interface ConfirmModalProps {
  open: boolean;
  title: string;
  message: string;
  onConfirm: () => void | Promise<void>;
  onCancel: () => void;
  confirmLabel?: string;
  cancelLabel?: string;
}

export function ConfirmModal({
  open,
  title,
  message,
  onConfirm,
  onCancel,
  confirmLabel = 'Confirmar',
  cancelLabel = 'Cancelar',
}: ConfirmModalProps) {
  return (
    <ModalShell open={open} title={title} onClose={onCancel}>
      <p style={{ marginBottom: '16px', color: 'var(--color-text-secondary)' }}>{message}</p>
      <div className="factions">
        <button className="btn-del" type="button" onClick={onConfirm}>
          {confirmLabel}
        </button>
        <button className="btn-cancel" type="button" onClick={onCancel}>
          {cancelLabel}
        </button>
      </div>
    </ModalShell>
  );
}
