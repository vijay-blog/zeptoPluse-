import 'package:flutter/foundation.dart';
import '../models/product.dart';
import '../services/api_service.dart';
import '../services/repositories.dart';

class CatalogProvider extends ChangeNotifier {
  final ApiService api = ApiService();
  List<Product> products = [];
  List<CategoryItem> categories = [];
  bool loading = false;
  String? error;
  Future<void> load() async {
    loading=true; error=null; notifyListeners();
    try {
      final results=await Future.wait([api.get('/products'),api.get('/categories')]);
      final pd=results[0], cd=results[1];
      final pl=pd is List?pd:(pd is Map?(pd['content']??const []):const []);
      products=(pl as List).whereType<Map<String,dynamic>>().map(Product.fromJson).toList();
      final cl=cd is List?cd:const [];
      categories=(cl as List).whereType<Map<String,dynamic>>().where((e)=>e['active']!=false).map((e)=>CategoryItem(name:(e['name']??'').toString(),icon:(e['imageUrl']??'').toString())).where((e)=>e.name.isNotEmpty).toList();
    } catch(e) { products=[]; categories=[]; error=e.toString(); }
    loading=false; notifyListeners();
  }
  List<Product> search(String q,{String? category}) { var l=products; if(category!=null&&category.isNotEmpty) l=l.where((p)=>p.category.toLowerCase()==category.toLowerCase()).toList(); if(q.trim().isNotEmpty){final s=q.toLowerCase();l=l.where((p)=>p.name.toLowerCase().contains(s)||p.brand.toLowerCase().contains(s)||p.category.toLowerCase().contains(s)).toList();} return l; }
}
