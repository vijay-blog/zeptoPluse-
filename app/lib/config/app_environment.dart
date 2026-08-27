import '../core/app_config.dart';

abstract final class AppEnvironment {
  static String get apiBaseUrl => AppConfig.apiBaseUrl;
}
