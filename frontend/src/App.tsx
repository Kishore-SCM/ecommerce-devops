import { Routes, Route, Navigate } from 'react-router-dom';
import { Navbar }            from '@/components/Navbar';
import { HomePage }          from '@/pages/HomePage';
import { ProductDetailPage } from '@/pages/ProductDetailPage';
import { CartPage }          from '@/pages/CartPage';
import { OrdersPage }        from '@/pages/OrdersPage';
import { LoginPage }         from '@/pages/LoginPage';
import { AdminDashboard }    from '@/pages/AdminDashboard';
import { useAuthStore }      from '@/store/authStore';

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = useAuthStore(s => s.token);
  return token ? <>{children}</> : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <Routes>
        <Route path="/"             element={<HomePage />} />
        <Route path="/products/:id" element={<ProductDetailPage />} />
        <Route path="/login"        element={<LoginPage />} />
        <Route path="/cart"         element={<PrivateRoute><CartPage /></PrivateRoute>} />
        <Route path="/orders"       element={<PrivateRoute><OrdersPage /></PrivateRoute>} />
        <Route path="/admin"        element={<PrivateRoute><AdminDashboard /></PrivateRoute>} />
      </Routes>
    </div>
  );
}
