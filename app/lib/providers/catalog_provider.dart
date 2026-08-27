import 'package:flutter/foundation.dart';
import '../data/mock_data.dart';
import '../models/product.dart';
import '../services/api_service.dart';

class CatalogProvider extends ChangeNotifier {
  final ApiService api = ApiService();
  List<Product> products = [];
  bool loading = false;
  String? error;
  Future<void> load() async {
    loading = true;
    notifyListeners();
    try {
      final data = await api.get('/products', {'page': '0', 'size': '100'});
      final list = (data is Map ? data['content'] : data) as List;
      products = list.map((e) => Product.fromJson(e)).toList();
    } catch (e) {
      products = List.of(mockProducts);
      error = null;
    }
    loading = false;
    notifyListeners();
  }

  List<Product> search(String q, {String? category}) {
    var l = products;
    if (category != null && category.isNotEmpty) {
      l = l
          .where((p) => p.category.toLowerCase() == category.toLowerCase())
          .toList();
    }
    if (q.trim().isNotEmpty) {
      final s = q.toLowerCase();
      l = l
          .where((p) =>
              p.name.toLowerCase().contains(s) ||
              p.brand.toLowerCase().contains(s) ||
              p.category.toLowerCase().contains(s))
          .toList();
    }
    return l;
  }
}
