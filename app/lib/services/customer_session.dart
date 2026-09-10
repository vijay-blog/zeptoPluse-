import 'package:shared_preferences/shared_preferences.dart';
import 'api_service.dart';

class CustomerSession {
  CustomerSession({ApiService? api}) : _api = api ?? ApiService();
  final ApiService _api;
  static Future<int>? _creationInFlight;

  Future<int> ensureCustomerId() async {
    final prefs=await SharedPreferences.getInstance();
    final existing=prefs.getInt('nm.customerId');
    if(existing!=null&&existing>0)return existing;
    _creationInFlight ??= _createCustomer();
    try { return await _creationInFlight!; } finally { _creationInFlight=null; }
  }

  Future<int> _createCustomer() async {
    final prefs=await SharedPreferences.getInstance();
    final existing=prefs.getInt('nm.customerId');
    if(existing!=null&&existing>0)return existing;
    var phone=prefs.getString('nm.guestPhone');
    if(phone==null||phone.isEmpty){final timestamp=DateTime.now().millisecondsSinceEpoch.toString();phone='9${timestamp.substring(timestamp.length-9)}';await prefs.setString('nm.guestPhone',phone);}
    final created=await _api.post('/customers',{'name':'Guest Customer','phone':phone,'email':null});
    final id=(created['id'] as num?)?.toInt()??0;
    if(id<=0)throw Exception('Unable to create guest customer');
    await prefs.setInt('nm.customerId',id);
    return id;
  }
}
