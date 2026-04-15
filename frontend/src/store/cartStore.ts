import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { Product } from '@/types';

interface CartItem { product: Product; quantity: number; }
interface CartStore {
  items: CartItem[];
  addItem: (product: Product) => void;
  removeItem: (productId: string) => void;
  updateQty: (productId: string, qty: number) => void;
  clearCart: () => void;
  totalItems: () => number;
  totalPrice: () => number;
}

export const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      items: [],
      addItem: (product) => set(state => {
        const existing = state.items.find(i => i.product.id === product.id);
        if (existing) return { items: state.items.map(i => i.product.id === product.id ? { ...i, quantity: i.quantity + 1 } : i) };
        return { items: [...state.items, { product, quantity: 1 }] };
      }),
      removeItem: (id) => set(state => ({ items: state.items.filter(i => i.product.id !== id) })),
      updateQty: (id, qty) => set(state => ({
        items: qty <= 0 ? state.items.filter(i => i.product.id !== id)
          : state.items.map(i => i.product.id === id ? { ...i, quantity: qty } : i)
      })),
      clearCart: () => set({ items: [] }),
      totalItems: () => get().items.reduce((s, i) => s + i.quantity, 0),
      totalPrice: () => get().items.reduce((s, i) => s + i.product.price * i.quantity, 0),
    }),
    { name: 'cart-storage' }
  )
);
