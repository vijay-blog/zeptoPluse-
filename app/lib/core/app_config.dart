class AppConfig {
  // Android emulator -> 10.0.2.2. For a physical phone, replace with your PC LAN IP.
  static const String apiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8080/api/v1',
  );
  static const bool useMockFallback = true;
  static const Duration timeout = Duration(seconds: 15);
}
