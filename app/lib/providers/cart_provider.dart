import 'package:flutter/foundation.dart';
import 'dart:convert';
import '../models/cart_item.dart';
import '../models/product.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CartProvider extends ChangeNotifier {
  final List<CartItem> items = [];
  bool initialized = false;

  CartProvider() {
    _load();
  }

  int get count => items.fold(0, (a, b) => a + b.quantity);
  double get subtotal => items.fold(0, (a, b) => a + b.total);
  double get delivery => subtotal == 0 ? 0 : (subtotal >= 499 ? 0 : 39);
  double get total => subtotal + delivery;

  Future<void> _load() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getStringList('zp.cart') ?? const [];
    items
      ..clear()
      ..addAll(raw.map((e) {
        final m = jsonDecode(e) as Map<String, dynamic>;
        return CartItem(
          product: Product.fromJson(m['product'] as Map<String, dynamic>),
          quantity: m['quantity'] as int? ?? 1,
        );
      }));
    initialized = true;
    notifyListeners();
  }

  Future<void> _persist() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setStringList(
      'zp.cart',
      items
          .map((x) => jsonEncode(
              {'product': x.product.toJson(), 'quantity': x.quantity}))
          .toList(),
    );
  }

  void add(Product p) {
    final i = items.indexWhere((x) => x.product.id == p.id);
    if (i < 0) {
      items.add(CartItem(product: p));
    } else {
      items[i].quantity++;
    }
    _persist();
    notifyListeners();
  }

  void remove(Product p) {
    final i = items.indexWhere((x) => x.product.id == p.id);
    if (i < 0) return;
    if (items[i].quantity <= 1) {
      items.removeAt(i);
    } else {
      items[i].quantity--;
    }
    _persist();
    notifyListeners();
  }

  void delete(Product p) {
    items.removeWhere((x) => x.product.id == p.id);
    _persist();
    notifyListeners();
  }

  void clear() {
    items.clear();
    _persist();
    notifyListeners();
  }
}
