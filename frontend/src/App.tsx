import { useCallback, useEffect, useState } from 'react';
import { OperationsLayout, type Screen } from './layouts';
import type { OperationsData } from './models';
import { CategoriesPage, DashboardPage, OrdersPage, ProductsPage } from './pages';
import { API_BASE_URL, operationsApi } from './services';

const emptyData: OperationsData = { products: [], categories: [], orders: [] };

export default function App() {
  const [screen, setScreen] = useState<Screen>('dashboard');
  const [data, setData] = useState<OperationsData>(emptyData);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadData = useCallback(async () => {
    setIsLoading(true);
    const [products, categories, orders] = await Promise.allSettled([
      operationsApi.products(), operationsApi.categories(), operationsApi.orders(),
    ]);
    const failures = [products, categories, orders].filter((result) => result.status === 'rejected');
    setData({
      products: products.status === 'fulfilled' ? products.value : [],
      categories: categories.status === 'fulfilled' ? categories.value : [],
      orders: orders.status === 'fulfilled' ? orders.value : [],
    });
    setError(failures.length ? `Some data could not be loaded. Check that the API is available at ${API_BASE_URL}.` : null);
    setIsLoading(false);
  }, []);

  useEffect(() => { void loadData(); }, [loadData]);

  return (
    <OperationsLayout activeScreen={screen} onNavigate={setScreen}>
      {error && <div className="api-notice" role="status"><strong>API connection notice</strong>{error}</div>}
      {screen === 'dashboard' && <DashboardPage data={data} isLoading={isLoading} onRefresh={() => void loadData()} />}
      {screen === 'products' && <ProductsPage products={data.products} isLoading={isLoading} />}
      {screen === 'categories' && <CategoriesPage categories={data.categories} isLoading={isLoading} />}
      {screen === 'orders' && <OrdersPage orders={data.orders} isLoading={isLoading} />}
    </OperationsLayout>
  );
}
