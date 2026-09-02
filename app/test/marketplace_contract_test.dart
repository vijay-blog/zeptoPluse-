import 'package:flutter_test/flutter_test.dart';
import 'package:nexamart_customer/models/cart_item.dart';
import 'package:nexamart_customer/models/order.dart';
import 'package:nexamart_customer/models/payment.dart';
import 'package:nexamart_customer/models/product.dart';

void main() {
  test('Product parses backend JSON and preserves server image asset', () {
    final product = Product.fromJson({
      'id': 42,
      'sku': 'ZP-MOB-001',
      'name': 'Mobile Phone',
      'brand': 'SmartTech',
      'categoryId': 15,
      'categoryName': 'Mobile Phones',
      'imageUrl': 'assets/images/products/mobile.png',
      'mrp': 16999,
      'sellingPrice': 14999,
      'discountPercentage': 11.76,
      'unit': '1 unit',
      'availability': true,
      'stockQuantity': 8,
      'deliveryType': 'MEDIUM',
    });

    expect(product.id, 42);
    expect(product.categoryId, '15');
    expect(product.imageAsset, 'assets/images/products/mobile.png');
    expect(product.available, isTrue);
  });

  test('Cart item total uses selling price and quantity', () {
    final item = CartItem(
      product: Product.fromJson({
        'id': 1,
        'name': 'Rice',
        'sellingPrice': 250,
        'mrp': 300,
        'unit': '5 kg',
      }),
      quantity: 2,
    );

    expect(item.total, 500);
  });

  test('Order response maps backend totals, status, payment and items', () {
    final order = CustomerOrder.fromJson({
      'id': 10,
      'orderNumber': 'ZP-123',
      'createdAt': '2026-08-26T18:40:00',
      'status': 'PAYMENT_PENDING',
      'paymentMethod': 'ONLINE',
      'paymentStatus': 'CREATED',
      'subtotal': 499,
      'deliveryFee': 0,
      'discountAmount': 20,
      'totalAmount': 479,
      'addressSnapshot': 'Madhapur, Hyderabad',
      'items': [
        {
          'productId': 1,
          'productName': 'Rice',
          'productBrand': 'India Gate',
          'productImageUrl': 'assets/images/products/rice.png',
          'productUnit': '5 kg',
          'quantity': 1,
          'unitPrice': 499,
          'lineTotal': 499,
        }
      ],
    });

    expect(order.orderNumber, 'ZP-123');
    expect(order.status, OrderStatus.paymentPending);
    expect(order.paymentMethod, 'ONLINE');
    expect(order.total, 479);
    expect(order.items.single.product.name, 'Rice');
  });

  test('Payment order maps Razorpay create-order response', () {
    final payment = PaymentOrder.fromJson({
      'paymentId': 7,
      'orderId': 10,
      'keyId': 'rzp_test_key',
      'gatewayOrderId': 'order_abc',
      'amount': 479,
      'currency': 'INR',
    });

    expect(payment.gatewayOrderId, 'order_abc');
    expect(payment.orderId, 10);
    expect(payment.currency, 'INR');
  });
}
