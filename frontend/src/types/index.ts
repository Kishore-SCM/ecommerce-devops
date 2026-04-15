export interface Product {
  id: string; name: string; description: string;
  price: number; stock: number; sku: string; category: string;
  imageUrls: string[]; rating: number; reviewCount: number;
  featured: boolean; createdAt: string;
}
export interface Order {
  id: string; userId: string; items: OrderItem[];
  totalAmount: number; status: OrderStatus;
  shippingAddress: string; trackingNumber: string | null; createdAt: string;
}
export interface OrderItem {
  id: string; productId: string; productName: string;
  quantity: number; unitPrice: number; subtotal: number;
}
export type OrderStatus = 'PENDING'|'CONFIRMED'|'PROCESSING'|'SHIPPED'|'DELIVERED'|'CANCELLED'|'REFUNDED';
export interface PaginatedResponse<T> {
  content: T[]; totalElements: number; totalPages: number;
  size: number; number: number; first: boolean; last: boolean;
}
export interface AuthResponse { token: string; userId: string; email: string; }
