import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import api from '@/api/axios';
import { ProductCard } from '@/components/ProductCard';
import { SearchBar }   from '@/components/SearchBar';
import type { Product, PaginatedResponse } from '@/types';

const CATEGORIES = ['All','Electronics','Clothing','Books','Home','Sports','Beauty'];

export function HomePage() {
  const [category, setCategory] = useState('All');
  const [search, setSearch]     = useState('');
  const [page, setPage]         = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ['products', category, search, page],
    queryFn:  () => api.get<PaginatedResponse<Product>>('/products', {
      params: { category: category === 'All' ? undefined : category.toUpperCase(), search: search || undefined, page, size: 12 }
    }).then(r => r.data),
  });

  const { data: featured } = useQuery({
    queryKey: ['featured'],
    queryFn:  () => api.get<Product[]>('/products/featured').then(r => r.data),
  });

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-indigo-600 text-white px-6 py-16 text-center">
        <h1 className="text-4xl font-bold mb-4">Discover Amazing Products</h1>
        <p className="text-lg opacity-90 mb-8">Shop thousands of items across all categories</p>
        <SearchBar value={search} onChange={setSearch} />
      </div>
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="flex gap-3 overflow-x-auto pb-3 mb-8">
          {CATEGORIES.map(cat => (
            <button key={cat} onClick={() => { setCategory(cat); setPage(0); }}
              className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition ${category === cat ? 'bg-indigo-600 text-white' : 'bg-white text-gray-600 border hover:border-indigo-300'}`}>
              {cat}
            </button>
          ))}
        </div>
        {!search && featured?.length && (
          <section className="mb-10">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Featured products</h2>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {featured.slice(0,4).map(p => <ProductCard key={p.id} product={p} />)}
            </div>
          </section>
        )}
        <section>
          <h2 className="text-xl font-bold text-gray-900 mb-4">{search ? `Results for "${search}"` : 'All products'}</h2>
          {isLoading ? (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {Array.from({length:8}).map((_,i) => <div key={i} className="h-72 bg-gray-200 rounded-2xl animate-pulse" />)}
            </div>
          ) : (
            <>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {data?.content.map(p => <ProductCard key={p.id} product={p} />)}
              </div>
              <div className="flex justify-center gap-2 mt-8">
                <button disabled={page===0} onClick={() => setPage(p=>p-1)} className="px-4 py-2 rounded-lg border disabled:opacity-40">Previous</button>
                <span className="px-4 py-2">Page {page+1} of {data?.totalPages}</span>
                <button disabled={data?.last} onClick={() => setPage(p=>p+1)} className="px-4 py-2 rounded-lg border disabled:opacity-40">Next</button>
              </div>
            </>
          )}
        </section>
      </div>
    </div>
  );
}
