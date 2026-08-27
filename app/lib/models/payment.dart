class PaymentOrder {
  final int paymentId;
  final int orderId;
  final String keyId;
  final String gatewayOrderId;
  final double amount;
  final String currency;

  const PaymentOrder({
    required this.paymentId,
    required this.orderId,
    required this.keyId,
    required this.gatewayOrderId,
    required this.amount,
    required this.currency,
  });

  factory PaymentOrder.fromJson(Map<String, dynamic> json) => PaymentOrder(
        paymentId: (json['paymentId'] as num).toInt(),
        orderId: (json['orderId'] as num).toInt(),
        keyId: json['keyId'].toString(),
        gatewayOrderId: json['gatewayOrderId'].toString(),
        amount: (json['amount'] as num).toDouble(),
        currency: json['currency']?.toString() ?? 'INR',
      );
}
