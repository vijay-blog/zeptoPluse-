import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../data/mock_data.dart';
import '../providers/catalog_provider.dart';
import 'category_screen.dart';

class AllCategoriesScreen extends StatelessWidget {
  const AllCategoriesScreen({super.key});
  @override
  Widget build(BuildContext c) {
    final provider = c.watch<CatalogProvider>();
    return Scaffold(
        appBar: AppBar(
            title: const Text('All Categories',
                style: TextStyle(fontWeight: FontWeight.w900))),
        body: GridView.builder(
            padding: const EdgeInsets.all(18),
            itemCount: categories.length,
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3,
                crossAxisSpacing: 12,
                mainAxisSpacing: 12,
                childAspectRatio: .86),
            itemBuilder: (_, i) {
              final x = categories[i];
              return InkWell(
                  borderRadius: BorderRadius.circular(18),
                  onTap: () => Navigator.push(
                      c,
                      MaterialPageRoute(
                          builder: (_) =>
                              CategoryScreen(category: x['name']!))),
                  child: Card(
                      child: Padding(
                          padding: const EdgeInsets.all(10),
                          child: Column(children: [
                            Expanded(
                                child: Image.asset(x['image']!,
                                    fit: BoxFit.contain)),
                            const SizedBox(height: 6),
                            Text(x['name']!,
                                textAlign: TextAlign.center,
                                maxLines: 2,
                                overflow: TextOverflow.ellipsis,
                                style: const TextStyle(
                                    fontWeight: FontWeight.w700, fontSize: 12)),
                            const SizedBox(height: 2),
                            Text(
                                '${provider.search('', category: x['name']!).length}+ items',
                                style: const TextStyle(
                                    fontSize: 10, color: Colors.black54))
                          ]))));
            }));
  }
}
