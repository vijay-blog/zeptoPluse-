import '../data/mock_data.dart';
import '../models/address.dart';
import '../models/cart_item.dart';
import '../models/order.dart';
import '../models/product.dart';

abstract class ProductRepository {
  Future<List<CategoryItem>> getCategories();
  Future<List<Product>> getProducts({
    int page = 1,
    int pageSize = 20,
    String? search,
    String? categoryId,
    String? sort,
  });
  Future<Product?> getProductById(String id);
  Future<List<Product>> getProductsByCategory(String categoryId);
  Future<List<Product>> searchProducts(String query);
}

abstract class OrderRepository {
  Future<CustomerOrder> createOrder({
    required List<CartItem> items,
    required Address address,
    double? subtotal,
    double? deliveryFee,
    double? discount,
    required double total,
  });
  Future<List<CustomerOrder>> getOrders();
  Future<CustomerOrder?> getOrderById(String orderId);
}

abstract class AddressRepository {
  Future<List<Address>> getAddresses();
  Future<Address> saveAddress(Address address);
  Future<void> deleteAddress(String id);
}

class CategoryItem {
  final String name;
  final String icon;

  const CategoryItem({required this.name, required this.icon});
}

class MockProductRepository implements ProductRepository {
  @override
  Future<List<CategoryItem>> getCategories() async {
    await Future.delayed(const Duration(milliseconds: 150));
    return categories
        .map((e) => CategoryItem(
              name: e['name'] ?? 'Other',
              icon: e['image'] ?? 'assets/images/products/oil.png',
            ))
        .toList();
  }

  @override
  Future<List<Product>> getProducts({
    int page = 1,
    int pageSize = 20,
    String? search,
    String? categoryId,
    String? sort,
  }) async {
    await Future.delayed(const Duration(milliseconds: 200));
    var list = List<Product>.from(mockProducts);

    if (categoryId != null && categoryId.isNotEmpty) {
      list = list
          .where((p) => p.category.toLowerCase() == categoryId.toLowerCase())
          .toList();
    }

    if (search != null && search.isNotEmpty) {
      final query = search.toLowerCase();
      list = list
          .where((p) =>
              p.name.toLowerCase().contains(query) ||
              p.brand.toLowerCase().contains(query) ||
              p.category.toLowerCase().contains(query))
          .toList();
    }

    if (sort != null && sort.isNotEmpty) {
      switch (sort) {
        case 'price_asc':
          list = [...list]
            ..sort((a, b) => a.sellingPrice.compareTo(b.sellingPrice));
          break;
        case 'price_desc':
          list = [...list]
            ..sort((a, b) => b.sellingPrice.compareTo(a.sellingPrice));
          break;
        case 'discount':
          list = [...list]..sort((a, b) => b.discount.compareTo(a.discount));
          break;
        default:
          list = [...list]..sort((a, b) => a.name.compareTo(b.name));
      }
    }

    final start = (page - 1) * pageSize;
    final end = start + pageSize;
    if (start >= list.length) return [];
    return list.sublist(start, end > list.length ? list.length : end);
  }

  @override
  Future<Product?> getProductById(String id) async {
    await Future.delayed(const Duration(milliseconds: 100));
    try {
      return mockProducts.firstWhere((p) => p.id.toString() == id);
    } catch (_) {
      return null;
    }
  }

  @override
  Future<List<Product>> getProductsByCategory(String categoryId) async {
    return getProducts(categoryId: categoryId);
  }

  @override
  Future<List<Product>> searchProducts(String query) async {
    return getProducts(search: query);
  }
}

class MockOrderRepository implements OrderRepository {
  final List<CustomerOrder> _orders = [];

  @override
  Future<CustomerOrder> createOrder({
    required List<CartItem> items,
    required Address address,
    double? subtotal,
    double? deliveryFee,
    double? discount,
    required double total,
  }) async {
    await Future.delayed(const Duration(milliseconds: 300));
    final orderTotal =
        total > 0 ? total : items.fold(0.0, (sum, i) => sum + i.total);
    final order = CustomerOrder(
      id: 'ZP${DateTime.now().millisecondsSinceEpoch.toString().substring(6)}',
      createdAt: DateTime.now(),
      items: items
          .map((e) => CartItem(product: e.product, quantity: e.quantity))
          .toList(),
      subtotal: subtotal ?? items.fold(0.0, (sum, i) => sum + i.total),
      deliveryFee: deliveryFee ?? 39.0,
      discount: discount ?? 0.0,
      total: orderTotal,
      address: address.fullAddress,
      paymentMethod: 'COD',
      status: OrderStatus.created,
    );
    _orders.insert(0, order);
    return order;
  }

  @override
  Future<List<CustomerOrder>> getOrders() async {
    await Future.delayed(const Duration(milliseconds: 150));
    return List.unmodifiable(_orders);
  }

  @override
  Future<CustomerOrder?> getOrderById(String orderId) async {
    await Future.delayed(const Duration(milliseconds: 100));
    try {
      return _orders.firstWhere((o) => o.id == orderId);
    } catch (_) {
      return null;
    }
  }
}

class MockAddressRepository implements AddressRepository {
  final List<Address> _addresses = [
    const Address(
      id: 'addr_1',
      name: 'Raghupathi',
      mobile: '9876543210',
      house: 'Flat 402, Sri Nilayam',
      street: 'Madhapur Main Road',
      area: 'Madhapur',
      city: 'Hyderabad',
      state: 'Telangana',
      pincode: '500081',
      isDefault: true,
    ),
  ];

  @override
  Future<List<Address>> getAddresses() async {
    await Future.delayed(const Duration(milliseconds: 150));
    return List.unmodifiable(_addresses);
  }

  @override
  Future<Address> saveAddress(Address address) async {
    await Future.delayed(const Duration(milliseconds: 200));
    if (_addresses.any((a) => a.id == address.id)) {
      final index = _addresses.indexWhere((a) => a.id == address.id);
      _addresses[index] = address;
    } else {
      _addresses.add(address);
    }
    return address;
  }

  @override
  Future<void> deleteAddress(String id) async {
    await Future.delayed(const Duration(milliseconds: 150));
    _addresses.removeWhere((a) => a.id == id);
  }
}
