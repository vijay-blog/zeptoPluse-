class AppConfig {
  static const String _apiPrefix = '/api/v1';
  static const String _defaultApiBaseUrl =
      'http://nexamart.railway.internal$_apiPrefix';
  static final String apiBaseUrl = _normalizeBaseUrl(
    String.fromEnvironment('API_BASE_URL', defaultValue: _defaultApiBaseUrl),
  );

  static String resolveEndpoint(String endpoint) {
    final normalized = endpoint.startsWith('/') ? endpoint : '/$endpoint';
    if (apiBaseUrl.endsWith(_apiPrefix) &&
        (normalized == _apiPrefix || normalized.startsWith('$_apiPrefix/'))) {
      final trimmed = normalized.substring(_apiPrefix.length);
      return trimmed.isEmpty ? '/' : trimmed;
    }
    return normalized;
  }

  static String _normalizeBaseUrl(String value) =>
      value.trim().replaceAll(RegExp(r'/+$'), '');

  static const bool useMockFallback = true;
  static const Duration timeout = Duration(seconds: 15);
}
