import type { CardBillItem } from '../domain/types';
import { CARD_ICONS } from '../ui/constants';

export function buildCardIconMap(
  cards: CardBillItem[] = [],
  baseIcons: Record<string, string> = CARD_ICONS
): Record<string, string> {
  const map: Record<string, string> = { ...baseIcons };

  cards.forEach((card) => {
    if (card.icon) map[card.id] = card.icon;
  });

  return map;
}
