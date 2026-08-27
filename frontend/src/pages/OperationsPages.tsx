import { DataPanel, EmptyState, LoadingState, MetricCard, StatusPill } from '../components';
import type { Category, OperationsData, Order, Product } from '../models';

function formatCurrency(value?: number) {
  if (value === undefined || value === null) return '—';
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(value);
}

function getOrderStatus(order: Order) {
  return order.orderStatus ?? order.status ?? 'PENDING';
}

export function DashboardPage({ data, isLoading, onRefresh }: { data: OperationsData; isLoading: boolean; onRefresh: () => void }) {
  const availableProducts = data.products.filter((product) => product.available !== false).length;
  const deliveredOrders = data.orders.filter((order) => getOrderStatus(order).toUpperCase() === 'DELIVERED').length;
  const revenue = data.orders.reduce((total, order) => total + (order.totalAmount ?? order.total ?? 0), 0);

  return (
    <>
      <section className="page-intro">
        <div><h2>Good morning, Operations team</h2><p>Here is the current marketplace activity across your catalog and orders.</p></div>
        <button className="refresh-button" type="button" onClick={onRefresh}>↻ Refresh data</button>
      </section>
      <section className="metric-grid" aria-label="Marketplace overview">
        <MetricCard label="Total products" value={isLoading ? '—' : data.products.length.toString()} detail="Catalog items" />
        <MetricCard label="Categories" value={isLoading ? '—' : data.categories.length.toString()} detail="Active catalog groups" />
        <MetricCard label="Orders received" value={isLoading ? '—' : data.orders.length.toString()} detail={`${deliveredOrders} delivered`} />
        <MetricCard label="Order value" value={isLoading ? '—' : formatCurrency(revenue)} detail="Across loaded orders" />
      </section>
      <section className="dashboard-grid">
        <article className="panel">
          <div className="panel-heading"><div><p className="eyebrow">CATALOG HEALTH</p><h3>Availability overview</h3></div><span className="subtle-badge">Live</span></div>
          <div className="availability"><strong>{availableProducts}</strong><span>products available to sell</span><div className="progress"><span style={{ width: `${data.products.length ? (availableProducts / data.products.length) * 100 : 0}%` }} /></div></div>
        </article>
        <article className="panel">
          <div className="panel-heading"><div><p className="eyebrow">QUICK START</p><h3>Next operations modules</h3></div></div>
          <ul className="future-list">
            <li><span>01</span><div><strong>Inventory controls</strong><p>Stock adjustments and replenishment alerts.</p></div></li>
            <li><span>02</span><div><strong>Fulfilment workflow</strong><p>Pick, pack, dispatch, and delivery actions.</p></div></li>
            <li><span>03</span><div><strong>Team permissions</strong><p>Role-based access for operations staff.</p></div></li>
          </ul>
        </article>
      </section>
    </>
  );
}

export function ProductsPage({ products, isLoading }: { products: Product[]; isLoading: boolean }) {
  return <DataPanel title="Product catalog" description="Products currently available in the marketplace.">
    {isLoading ? <LoadingState /> : products.length === 0 ? <EmptyState message="No products returned by the API." /> : (
      <div className="table-wrap"><table><thead><tr><th>Product</th><th>Category</th><th>Price</th><th>Stock</th><th>Status</th></tr></thead>
        <tbody>{products.map((product) => <tr key={product.id}><td><strong>{product.name}</strong><small>#{product.id}</small></td><td>{product.category ?? 'Unassigned'}</td><td>{formatCurrency(product.sellingPrice ?? product.price)}</td><td>{product.stock ?? '—'}</td><td><StatusPill active={product.available !== false} label={product.available === false ? 'Unavailable' : 'Available'} /></td></tr>)}</tbody>
      </table></div>
    )}
  </DataPanel>;
}

export function CategoriesPage({ categories, isLoading }: { categories: Category[]; isLoading: boolean }) {
  return <DataPanel title="Categories" description="Catalog grouping used to organize the storefront.">
    {isLoading ? <LoadingState /> : categories.length === 0 ? <EmptyState message="No categories returned by the API." /> : (
      <div className="table-wrap"><table><thead><tr><th>Category</th><th>Description</th><th>Products</th><th>Status</th></tr></thead>
        <tbody>{categories.map((category) => <tr key={category.id}><td><strong>{category.name}</strong><small>#{category.id}</small></td><td>{category.description ?? '—'}</td><td>{category.productCount ?? '—'}</td><td><StatusPill active={category.active !== false} label={category.active === false ? 'Inactive' : 'Active'} /></td></tr>)}</tbody>
      </table></div>
    )}
  </DataPanel>;
}

export function OrdersPage({ orders, isLoading }: { orders: Order[]; isLoading: boolean }) {
  return <DataPanel title="Order queue" description="Incoming marketplace orders and their current processing status.">
    {isLoading ? <LoadingState /> : orders.length === 0 ? <EmptyState message="No orders returned by the API." /> : (
      <div className="table-wrap"><table><thead><tr><th>Order</th><th>Customer</th><th>Created</th><th>Total</th><th>Status</th></tr></thead>
        <tbody>{orders.map((order) => <tr key={order.id}><td><strong>{order.orderNumber ?? `#${order.id}`}</strong><small>#{order.id}</small></td><td>{order.customerName ?? '—'}</td><td>{order.createdAt ? new Date(order.createdAt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) : '—'}</td><td>{formatCurrency(order.totalAmount ?? order.total)}</td><td><StatusPill active={getOrderStatus(order).toUpperCase() === 'DELIVERED'} label={getOrderStatus(order).replace(/_/g, ' ')} /></td></tr>)}</tbody>
      </table></div>
    )}
  </DataPanel>;
}
