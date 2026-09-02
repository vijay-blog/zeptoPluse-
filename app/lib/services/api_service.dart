import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;

import '../core/api_client.dart';
import '../core/app_config.dart';

class ApiService {
  final http.Client client;

  ApiService({http.Client? client}) : client = client ?? http.Client();

  Uri _uri(String path, [Map<String, String>? query]) {
    return Uri.parse('${AppConfig.apiBaseUrl}${AppConfig.resolveEndpoint(path)}')
        .replace(queryParameters: query);
  }

  Future<dynamic> get(String path, [Map<String, String>? query]) async {
    return _send(() => client.get(_uri(path, query), headers: _headers));
  }

  Future<dynamic> post(String path, Map<String, dynamic> body) async {
    return _send(() => client.post(
          _uri(path),
          headers: _headers,
          body: jsonEncode(body),
        ));
  }

  Map<String, String> get _headers => const {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      };

  Future<dynamic> _send(Future<http.Response> Function() request) async {
    final configIssue = AppConfig.runtimeConfigurationIssue;
    if (configIssue != null) throw ApiException(configIssue, 0);

    try {
      final response = await request().timeout(AppConfig.timeout);
      return _handle(response);
    } on TimeoutException catch (e, st) {
      _debugLog('Timeout', e, st);
      throw ApiException(
        'Unable to connect to NexaMart. Please check your internet connection.',
        0,
      );
    } on SocketException catch (e, st) {
      _debugLog('SocketException', e, st);
      throw ApiException(
        'Unable to connect to NexaMart. Please check your internet connection.',
        0,
      );
    } on http.ClientException catch (e, st) {
      _debugLog('ClientException', e, st);
      throw ApiException(
        'Unable to connect to NexaMart. Please check your internet connection.',
        0,
      );
    } on FormatException catch (e, st) {
      _debugLog('FormatException', e, st);
      throw ApiException(
        'We could not process the server response. Please try again.',
        0,
      );
    } catch (e, st) {
      if (e is ApiException) rethrow;
      _debugLog('Unexpected API error', e, st);
      throw ApiException(
        'Unable to connect to NexaMart. Please check your internet connection.',
        0,
      );
    }
  }

  dynamic _handle(http.Response response) {
    final statusCode = response.statusCode;
    if (statusCode >= 200 && statusCode < 300) {
      if (response.body.isEmpty) return null;
      return jsonDecode(response.body);
    }
    throw ApiException(_messageForStatus(statusCode), statusCode);
  }

  String _messageForStatus(int code) {
    switch (code) {
      case 400:
        return 'Bad request. Please check your input.';
      case 401:
        return 'Your session has expired. Please sign in again.';
      case 403:
        return "You don't have permission to perform this action.";
      case 404:
        return "We couldn't find the requested information.";
      case 409:
        return 'This item or order was updated. Please try again.';
      case 422:
        return 'Validation error. Please check submitted data.';
      case 429:
        return 'Too many requests. Please try again in a moment.';
      case 500:
      case 502:
      case 503:
        return 'NexaMart is temporarily unavailable. Please try again shortly.';
      default:
        return code >= 500
            ? 'NexaMart is temporarily unavailable. Please try again shortly.'
            : 'Unable to complete your request right now.';
    }
  }

  void _debugLog(String label, Object error, StackTrace stackTrace) {
    if (kReleaseMode) return;
    debugPrint('[ApiService] $label: $error');
    debugPrint('$stackTrace');
  }
}
