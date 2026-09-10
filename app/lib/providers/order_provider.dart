import 'package:flutter/foundation.dart';
import 'dart:convert';
import '../models/order.dart';
import '../models/cart_item.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/address.dart';
import '../models/payment.dart';
import '../services/api_service.dart';
import '../services/customer_session.dart';

class OrderProvider extends ChangeNotifier {
  final List<CustomerOrder> orders = [];
  bool initialized = false;
  final ApiService _api = ApiService();
  final CustomerSession _session = CustomerSession();

  OrderProvider() {
    _load();
  }

  Future<void> _load() async {
    final prefs=await SharedPreferences.getInstance();
    final raw=prefs.getStringList('zp.orders')??const [];
    orders..clear()..addAll(raw.map((x)=>CustomerOrder.fromJson(jsonDecode(x) as Map<String,dynamic>)));
    try { final customerId=await _session.ensureCustomerId(); final data=await _api.get('/orders',{'customerId':'$customerId'}); if(data is List){orders..clear()..addAll(data.whereType<Map<String,dynamic>>().map(CustomerOrder.fromJson)); await _persist();} } catch(_) {}
    initialized=true; notifyListeners();
  }

  Future<void> _persist() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setStringList(
      'zp.orders',
      orders.map((x) => jsonEncode(x.toJson())).toList(),
    );
  }

  Future<CustomerOrder> create(
    List<CartItem> items,
    Address address, {
    String paymentMethod = 'COD',
  }) async {
    final customerId = await _session.ensureCustomerId();
    final response = await _api.post('/orders', {
      'customerId': customerId,
      'address': address.toJson(),
      'paymentMethod': paymentMethod,
      'idempotencyKey':
          'checkout_${customerId}_${DateTime.now().millisecondsSinceEpoch}',
      'items': items
          .map((x) => {'productId': x.product.id, 'quantity': x.quantity})
          .toList(),
    });
    final order = CustomerOrder.fromJson(response);
    orders.removeWhere((o) => o.id == order.id);
    orders.insert(0, order);
    await _persist();
    notifyListeners();
    return order;
  }

  Future<PaymentOrder> createPaymentOrder(CustomerOrder order) async {
    final response = await _api.post('/payments/create-order', {
      'orderId': int.parse(order.id),
    });
    return PaymentOrder.fromJson(response);
  }

  Future<CustomerOrder> verifyPayment({
    required int orderId,
    required String gatewayOrderId,
    required String gatewayPaymentId,
    required String gatewaySignature,
  }) async {
    final response = await _api.post('/payments/verify', {
      'orderId': orderId,
      'gatewayOrderId': gatewayOrderId,
      'gatewayPaymentId': gatewayPaymentId,
      'gatewaySignature': gatewaySignature,
    });
    final order = CustomerOrder.fromJson(response);
    orders.removeWhere((o) => o.id == order.id);
    orders.insert(0, order);
    await _persist();
    notifyListeners();
    return order;
  }
}
