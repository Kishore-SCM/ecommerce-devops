import { ShoppingCart, Star, Heart } from 'lucide-react';
import { useCartStore } from '@/store/cartStore';
import { toast } from 'react-hot-toast';
import type { Product } from '@/types';

export function ProductCard({ product }: { product: Product }) {
  const addItem = useCartStore(s => s.addItem);
  return (
    <div className="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-lg transition-shadow group">
      <div className="relative overflow-hidden h-56">
        <img src={product.imageUrls?.[0] || '/placeholder.jpg'} alt={product.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" loading="lazy" />
        {product.featured && (
          <span className="absolute top-3 left-3 bg-indigo-600 text-white text-xs font-semibold px-2 py-1 rounded-full">Featured</span>
        )}
      </div>
      <div className="p-4">
        <p className="text-xs text-indigo-600 font-medium uppercase tracking-wide mb-1">{product.category}</p>
        <h3 className="font-semibold text-gray-900 text-sm line-clamp-2 mb-2">{product.name}</h3>
        <div className="flex items-center gap-1 mb-3">
          <Star size={14} className="text-amber-400 fill-amber-400" />
          <span className="text-sm text-gray-600">{product.rating?.toFixed(1)} ({product.reviewCount})</span>
        </div>
        <div className="flex items-center justify-between">
          <span className="text-xl font-bold">${product.price.toFixed(2)}</span>
          <button onClick={() => { addItem(product); toast.success(`${product.name} added!`); }}
            disabled={product.stock === 0}
            className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white text-sm font-medium px-3 py-2 rounded-xl transition">
            <ShoppingCart size={16} /> Add
          </button>
        </div>
      </div>
    </div>
  );
}
