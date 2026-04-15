import { Link, useNavigate } from 'react-router-dom';
import { ShoppingCart, LogOut, User } from 'lucide-react';
import { useCartStore } from '@/store/cartStore';
import { useAuthStore } from '@/store/authStore';

export function Navbar() {
  const totalItems = useCartStore(s => s.totalItems());
  const { token, logout } = useAuthStore();
  const navigate = useNavigate();
  return (
    <nav className="bg-indigo-600 text-white sticky top-0 z-50 shadow-md">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link to="/" className="text-xl font-bold">ShopWave</Link>
        <div className="flex items-center gap-4">
          <Link to="/cart" className="relative">
            <ShoppingCart size={22} />
            {totalItems > 0 && (
              <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold w-5 h-5 rounded-full flex items-center justify-center">{totalItems}</span>
            )}
          </Link>
          {token ? (
            <>
              <Link to="/orders"><User size={22} /></Link>
              <button onClick={() => { logout(); navigate('/login'); }}><LogOut size={20} /></button>
            </>
          ) : (
            <Link to="/login" className="text-sm bg-white/20 hover:bg-white/30 px-4 py-1.5 rounded-full">Sign in</Link>
          )}
        </div>
      </div>
    </nav>
  );
}
