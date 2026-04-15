import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface UserInfo { id: string; email: string; roles: string[]; }
interface AuthStore {
  token: string | null; user: UserInfo | null;
  setAuth: (token: string, user: UserInfo) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      token: null, user: null,
      setAuth: (token, user) => set({ token, user }),
      logout: () => set({ token: null, user: null }),
    }),
    { name: 'auth-storage' }
  )
);
