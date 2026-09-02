import 'dart:convert';
import 'package:http/http.dart' as http;
import '../core/app_config.dart';

class ApiService {
  final http.Client client;
  ApiService({http.Client? client}) : client = client ?? http.Client();
  Uri u(String path, [Map<String, String>? q]) =>
      Uri.parse('${AppConfig.apiBaseUrl}${AppConfig.resolveEndpoint(path)}')
          .replace(queryParameters: q);
  Future<dynamic> get(String path, [Map<String, String>? q]) async {
    final r = await client.get(u(path, q),
        headers: {'Accept': 'application/json'}).timeout(AppConfig.timeout);
    return _handle(r);
  }

  Future<dynamic> post(String path, Map<String, dynamic> body) async {
    final r = await client
        .post(u(path),
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            body: jsonEncode(body))
        .timeout(AppConfig.timeout);
    return _handle(r);
  }

  dynamic _handle(http.Response r) {
    if (r.statusCode < 200 || r.statusCode >= 300) throw Exception(_message(r));
    return r.body.isEmpty ? null : jsonDecode(r.body);
  }

  String _message(http.Response r) {
    try {
      final j = jsonDecode(r.body);
      return j['message'] ?? 'Server error (${r.statusCode})';
    } catch (_) {
      return 'Server error (${r.statusCode})';
    }
  }
}
