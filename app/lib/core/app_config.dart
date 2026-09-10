class AppConfig {
  static const String _apiPrefix = '/api/v1';
  static const String _defaultApiBaseUrl = 'https://zeptopluse-production.up.railway.app$_apiPrefix';
  static const bool _isReleaseBuild = bool.fromEnvironment('dart.vm.product');
  static final String apiBaseUrl = _normalizeBaseUrl(
    String.fromEnvironment('API_BASE_URL', defaultValue: _defaultApiBaseUrl),
  );

  static String? get runtimeConfigurationIssue {
    final value = apiBaseUrl.toLowerCase();
    if (!_isReleaseBuild) return null;
    if (value.isEmpty || !value.startsWith('https://')) {
      return 'NexaMart is not configured for production yet.';
    }
    if (value.contains('railway.internal') ||
        value.contains('10.0.2.2') ||
        value.contains('127.0.0.1') ||
        value.contains('localhost')) {
      return 'NexaMart is not configured for production yet.';
    }
    return null;
  }

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

  static const bool useMockFallback = false;
  static const Duration timeout = Duration(seconds: 15);
}
