import 'package:flutter/foundation.dart';
import 'dart:convert';
import '../models/address.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/api_service.dart';
import '../services/customer_session.dart';

class AddressProvider extends ChangeNotifier {
  final List<Address> addresses = [];
  Address? selected;
  bool initialized = false;
  final ApiService _api = ApiService();
  final CustomerSession _session = CustomerSession();

  AddressProvider() {
    _load();
  }

  Future<void> _load() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getStringList('zp.addresses') ?? const [];
    final saved = raw
        .map((x) => Address.fromJson(jsonDecode(x) as Map<String, dynamic>))
        .toList();
    addresses
      ..clear()
      ..addAll(saved);
    final selectedId = prefs.getString('zp.selectedAddressId');
    if (selectedId != null) {
      selected = addresses.where((x) => x.id == selectedId).firstOrNull;
    }
    selected ??= addresses.where((x) => x.isDefault).firstOrNull;
    selected ??= addresses.isEmpty ? null : addresses.first;
    try { final customerId=await _session.ensureCustomerId(); final data=await _api.get('/customers/$customerId/addresses'); if(data is List){ addresses..clear()..addAll(data.whereType<Map<String,dynamic>>().map(Address.fromJson)); selected=addresses.where((x)=>x.isDefault).firstOrNull ?? (addresses.isEmpty?null:addresses.first); await _persist(); } } catch(_) {}
    initialized = true;
    notifyListeners();
  }

  Future<void> _persist() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setStringList(
      'zp.addresses',
      addresses.map((x) => jsonEncode(x.toJson())).toList(),
    );
    if (selected?.id != null) {
      await prefs.setString('zp.selectedAddressId', selected!.id!);
    }
  }

  Future<void> save(Address a) async {
    try { final customerId=await _session.ensureCustomerId(); final body=a.toJson(); body.remove('id'); final dynamic data; if(a.id!=null&&int.tryParse(a.id!)!=null){data=await _api.put('/addresses/${a.id}',body);}else{data=await _api.post('/customers/$customerId/addresses',body);} if(data is Map<String,dynamic>){final saved=Address.fromJson(data); if(saved.isDefault)for(int i=0;i<addresses.length;i++)addresses[i]=addresses[i].copyWith(isDefault:false); final idx=addresses.indexWhere((x)=>x.id==saved.id); if(idx>=0)addresses[idx]=saved;else addresses.insert(0,saved);selected=saved;await _persist();notifyListeners();return;} } catch(_) {}
    final id=(a.id==null||a.id!.isEmpty)?'addr_${DateTime.now().millisecondsSinceEpoch}':a.id!; final item=a.copyWith(id:id); final idx=addresses.indexWhere((x)=>x.id==item.id); if(item.isDefault)for(int i=0;i<addresses.length;i++)addresses[i]=addresses[i].copyWith(isDefault:false); if(idx>=0)addresses[idx]=item;else addresses.insert(0,item);selected=item;await _persist();notifyListeners();
  }

  Future<void> select(Address a) async {
    selected = a;
    await _persist();
    notifyListeners();
  }

  Future<void> setDefault(Address a) async {
    for (int i = 0; i < addresses.length; i++) {
      addresses[i] = addresses[i].copyWith(isDefault: addresses[i].id == a.id);
    }
    selected = addresses.where((x) => x.id == a.id).firstOrNull;
    await _persist();
    notifyListeners();
  }

  Future<void> delete(Address a) async {
    if(a.id!=null&&int.tryParse(a.id!)!=null){try{await _api.delete('/addresses/${a.id}');}catch(_){}}
    addresses.remove(a);
    if (selected == a) selected = addresses.isEmpty ? null : addresses.first;
    await _persist();
    notifyListeners();
  }
}

extension<T> on Iterable<T> {
  T? get firstOrNull => isEmpty ? null : first;
}
