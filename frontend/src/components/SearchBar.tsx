import { Search } from 'lucide-react';

interface Props { value: string; onChange: (v: string) => void; }

export function SearchBar({ value, onChange }: Props) {
  return (
    <div className="relative max-w-md mx-auto">
      <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
      <input
        type="text"
        value={value}
        onChange={e => onChange(e.target.value)}
        placeholder="Search products..."
        className="w-full pl-10 pr-4 py-3 rounded-xl border border-white/30 bg-white/20 text-white placeholder-white/70 focus:outline-none focus:bg-white/30"
      />
    </div>
  );
}
