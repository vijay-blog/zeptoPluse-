import 'package:shared_preferences/shared_preferences.dart';

import 'api_service.dart';

class CustomerSession {
  CustomerSession({ApiService? api}) : _api = api ?? ApiService();

  final ApiService _api;

  Future<int> ensureCustomerId() async {
    final prefs = await SharedPreferences.getInstance();
    final existing = prefs.getInt('zp.customerId');
    if (existing != null && existing > 0) return existing;

    var phone = prefs.getString('zp.guestPhone');
    if (phone == null || phone.isEmpty) {
      final timestamp = DateTime.now().millisecondsSinceEpoch.toString();
      final suffix = timestamp.substring(timestamp.length - 9);
      phone = '9$suffix';
      await prefs.setString('zp.guestPhone', phone);
    }

    final created = await _api.post('/customers', {
      'name': 'Guest Customer',
      'phone': phone,
      'email': null,
    });
    final id = (created['id'] as num?)?.toInt() ?? 0;
    if (id <= 0) throw Exception('Unable to create guest customer');
    await prefs.setInt('zp.customerId', id);
    return id;
  }
}
