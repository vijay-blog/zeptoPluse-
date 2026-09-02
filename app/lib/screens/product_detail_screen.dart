import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/product.dart';
import '../providers/cart_provider.dart';
import 'cart_screen.dart';

class ProductDetailScreen extends StatelessWidget {
  final Product product;

  const ProductDetailScreen({super.key, required this.product});

  @override
  Widget build(BuildContext context) {
    final cart = context.watch<CartProvider>();
    final item =
        cart.items.where((x) => x.product.id == product.id).firstOrNull;

    return Scaffold(
      appBar: AppBar(
        actions: [
          IconButton(
            onPressed: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const CartScreen()),
            ),
            icon: Badge(
              isLabelVisible: cart.count > 0,
              label: Text('${cart.count}'),
              child: const Icon(Icons.shopping_bag_outlined),
            ),
          ),
        ],
      ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 14),
          child: Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: () => cart.add(product),
                  child: const Text('ADD TO CART'),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: FilledButton(
                  onPressed: () => cart.add(product),
                  child: const Text('BUY NOW'),
                ),
              ),
            ],
          ),
        ),
      ),
      body: ListView(
        children: [
          Container(
            height: 330,
            color: const Color(0xfff2f5ff),
            child: Image.asset(product.imageAsset, fit: BoxFit.contain),
          ),
          Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                if (product.discount >= 1)
                  Container(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: const Color(0xffe9edff),
                      borderRadius: BorderRadius.circular(6),
                    ),
                    child: Text(
                      '${product.discount.round()}% OFF',
                      style: const TextStyle(
                        color: Color(0xff3454d1),
                        fontWeight: FontWeight.w900,
                      ),
                    ),
                  ),
                const SizedBox(height: 8),
                Text(
                  product.name,
                  style: const TextStyle(
                      fontSize: 25, fontWeight: FontWeight.w900),
                ),
                const SizedBox(height: 5),
                Text(
                  product.brand,
                  style: TextStyle(color: Colors.grey.shade600),
                ),
                const SizedBox(height: 6),
                const Row(
                  children: [
                    Icon(Icons.star, color: Colors.amber, size: 18),
                    SizedBox(width: 4),
                    Text('4.4 • 120+ reviews'),
                  ],
                ),
                const SizedBox(height: 14),
                Row(
                  children: [
                    Text(
                      '₹${product.sellingPrice.round()}',
                      style: const TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.w900,
                      ),
                    ),
                    const SizedBox(width: 10),
                    Text(
                      '₹${product.mrp.round()}',
                      style: TextStyle(
                        color: Colors.grey.shade500,
                        decoration: TextDecoration.lineThrough,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 18),
                Text(
                  product.unit,
                  style: const TextStyle(fontWeight: FontWeight.w700),
                ),
                const SizedBox(height: 10),
                Text(
                  'Availability: ${product.available ? 'In Stock' : 'Out of Stock'}',
                  style: TextStyle(
                      color: product.available
                          ? const Color(0xff3454d1)
                          : Colors.red,
                      fontWeight: FontWeight.w700),
                ),
                const SizedBox(height: 10),
                Text(
                  'Delivery Type: ${product.deliveryType}',
                  style: const TextStyle(fontWeight: FontWeight.w600),
                ),
                const SizedBox(height: 18),
                const Text(
                  'About this product',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900),
                ),
                const SizedBox(height: 8),
                Text(
                  product.description,
                  style: const TextStyle(height: 1.5, color: Colors.black54),
                ),
                const SizedBox(height: 20),
                const ListTile(
                  contentPadding: EdgeInsets.zero,
                  leading: Icon(Icons.local_shipping_outlined),
                  title: Text('Delivery in Hyderabad'),
                  subtitle: Text(
                    'Availability and delivery time will be confirmed by the backend.',
                  ),
                ),
                const ListTile(
                  contentPadding: EdgeInsets.zero,
                  leading: Icon(Icons.list_alt_outlined),
                  title: Text('Specifications'),
                  subtitle: Text(
                      'Brand, unit, weight and category details available.'),
                ),
                if (item != null)
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: const Color(0xffe9edff),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      '${item.quantity} in your cart',
                      style: const TextStyle(
                        fontWeight: FontWeight.w800,
                        color: Color(0xff3454d1),
                      ),
                    ),
                  ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

extension<T> on Iterable<T> {
  T? get firstOrNull => isEmpty ? null : first;
}
