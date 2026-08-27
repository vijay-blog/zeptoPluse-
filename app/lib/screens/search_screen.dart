import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/catalog_provider.dart';
import '../widgets/product_card.dart';

class SearchScreen extends StatefulWidget {
  const SearchScreen({super.key});
  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final controller = TextEditingController();
  bool searching = false;

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  void onSearchChanged() {
    setState(() => searching = true);
    Future.delayed(const Duration(milliseconds: 220), () {
      if (mounted) setState(() => searching = false);
    });
  }

  @override
  Widget build(BuildContext c) {
    final catalog = c.watch<CatalogProvider>();
    final products = catalog.search(controller.text);
    return Scaffold(
        appBar: AppBar(
            title: TextField(
                controller: controller,
                autofocus: true,
                onChanged: (_) => onSearchChanged(),
                decoration: InputDecoration(
                    hintText: 'Search products...',
                    prefixIcon: const Icon(Icons.search),
                    suffixIcon: controller.text.isEmpty
                        ? null
                        : IconButton(
                            onPressed: () {
                              controller.clear();
                              setState(() {});
                            },
                            icon: const Icon(Icons.close)),
                    border: InputBorder.none))),
        body: catalog.error != null
            ? Center(child: Text(catalog.error!))
            : searching
                ? const Center(child: CircularProgressIndicator())
                : products.isEmpty
                    ? const Center(
                        child:
                            Column(mainAxisSize: MainAxisSize.min, children: [
                        Icon(Icons.search_off, size: 56, color: Colors.grey),
                        SizedBox(height: 10),
                        Text('No products found')
                      ]))
                    : GridView.builder(
                        padding: const EdgeInsets.all(16),
                        itemCount: products.length,
                        gridDelegate:
                            const SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: 2,
                                crossAxisSpacing: 12,
                                mainAxisSpacing: 12,
                                childAspectRatio: .63),
                        itemBuilder: (_, i) =>
                            ProductCard(product: products[i])));
  }
}
