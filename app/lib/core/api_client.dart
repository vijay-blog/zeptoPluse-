import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'app_config.dart';

enum HttpMethod { get, post, put, delete }

class ApiException implements Exception {
  final String message;
  final int statusCode;
  ApiException(this.message, this.statusCode);

  @override
  String toString() => message;
}

class ApiClient {
  final http.Client _client;
  String? _accessToken;

  ApiClient({http.Client? client}) : _client = client ?? http.Client();

  void setAccessToken(String? token) {
    _accessToken = token;
  }

  Future<dynamic> request(
    HttpMethod method,
    String endpoint, {
    Map<String, dynamic>? queryParameters,
    Map<String, dynamic>? body,
  }) async {
    final uri = Uri.parse('${AppConfig.apiBaseUrl}$endpoint').replace(
      queryParameters:
          queryParameters?.map((key, value) => MapEntry(key, value.toString())),
    );

    final headers = <String, String>{
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    if (_accessToken != null && _accessToken!.isNotEmpty) {
      headers['Authorization'] = 'Bearer $_accessToken';
    }

    http.Response response;
    try {
      switch (method) {
        case HttpMethod.get:
          response = await _client
              .get(uri, headers: headers)
              .timeout(AppConfig.timeout);
          break;
        case HttpMethod.post:
          response = await _client
              .post(uri,
                  headers: headers,
                  body: body != null ? jsonEncode(body) : null)
              .timeout(AppConfig.timeout);
          break;
        case HttpMethod.put:
          response = await _client
              .put(uri,
                  headers: headers,
                  body: body != null ? jsonEncode(body) : null)
              .timeout(AppConfig.timeout);
          break;
        case HttpMethod.delete:
          response = await _client
              .delete(uri, headers: headers)
              .timeout(AppConfig.timeout);
          break;
      }
    } on SocketException {
      throw ApiException('No internet connection or server unreachable.', 0);
    } on FormatException {
      throw ApiException('Bad response format from server.', 0);
    } catch (e) {
      if (e is ApiException) rethrow;
      throw ApiException('Network error occurred: ${e.toString()}', 0);
    }

    return _handleResponse(response);
  }

  dynamic _handleResponse(http.Response response) {
    final code = response.statusCode;
    if (code >= 200 && code < 300) {
      if (response.body.isEmpty) return null;
      try {
        return jsonDecode(response.body);
      } catch (_) {
        return response.body;
      }
    }

    String message;
    switch (code) {
      case 400:
        message = 'Bad request. Please check your input.';
        break;
      case 401:
        message = 'Unauthorized. Please login again.';
        break;
      case 403:
        message = 'Access forbidden.';
        break;
      case 404:
        message = 'Requested resource not found.';
        break;
      case 409:
        message = 'Conflict occurred. Please try again.';
        break;
      case 422:
        message = 'Validation error. Please check submitted data.';
        break;
      default:
        if (code >= 500) {
          message = 'Server error. Please try again later.';
        } else {
          message = 'Unexpected error occurred (Code $code).';
        }
    }

    try {
      final bodyMap = jsonDecode(response.body);
      if (bodyMap is Map && bodyMap.containsKey('message')) {
        message = bodyMap['message'].toString();
      }
    } catch (_) {}

    throw ApiException(message, code);
  }
}
