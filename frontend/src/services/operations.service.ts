import type { Category, Order, Product } from '../models';

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1').replace(/\/$/, '');

type ApiEnvelope<T> = T[] | { content?: T[]; data?: T[]; products?: T[]; categories?: T[]; orders?: T[] };

function unpack<T>(payload: ApiEnvelope<T>): T[] {
  if (Array.isArray(payload)) return payload;
  return payload.content ?? payload.data ?? payload.products ?? payload.categories ?? payload.orders ?? [];
}

async function getCollection<T>(path: string): Promise<T[]> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { Accept: 'application/json' },
  });

  if (!response.ok) throw new Error(`${path} returned ${response.status}`);
  return unpack<T>(await response.json() as ApiEnvelope<T>);
}

export const operationsApi = {
  products: () => getCollection<Product>('/products'),
  categories: () => getCollection<Category>('/categories'),
  orders: () => getCollection<Order>('/orders?customerId='),
};

export { API_BASE_URL };
