export type Identifier = string | number;

export interface Product {
  id: Identifier;
  name: string;
  category?: string;
  price?: number;
  sellingPrice?: number;
  available?: boolean;
  stock?: number;
}

export interface Category {
  id: Identifier;
  name: string;
  description?: string;
  active?: boolean;
  productCount?: number;
}

export type OrderStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'PROCESSING'
  | 'OUT_FOR_DELIVERY'
  | 'DELIVERED'
  | 'CANCELLED'
  | string;

export interface Order {
  id: Identifier;
  orderNumber?: string;
  customerName?: string;
  totalAmount?: number;
  total?: number;
  orderStatus?: OrderStatus;
  status?: OrderStatus;
  createdAt?: string;
}

export interface OperationsData {
  products: Product[];
  categories: Category[];
  orders: Order[];
}
