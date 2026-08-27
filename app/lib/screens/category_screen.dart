import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/catalog_provider.dart';
import '../widgets/product_card.dart';

class CategoryScreen extends StatelessWidget {
  final String category;
  const CategoryScreen({super.key, required this.category});
  @override
  Widget build(BuildContext c) {
    final list = c.watch<CatalogProvider>().search('', category: category);
    return Scaffold(
        appBar: AppBar(
            title: Text(category,
                style: const TextStyle(fontWeight: FontWeight.w900))),
        body: list.isEmpty
            ? const Center(
                child: Column(mainAxisSize: MainAxisSize.min, children: [
                Icon(Icons.inventory_2_outlined, size: 52, color: Colors.grey),
                SizedBox(height: 10),
                Text('No products available in this category'),
              ]))
            : GridView.builder(
                padding: const EdgeInsets.all(16),
                itemCount: list.length,
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 12,
                    mainAxisSpacing: 12,
                    childAspectRatio: .63),
                itemBuilder: (_, i) => ProductCard(product: list[i])));
  }
}
