import 'package:flutter/material.dart';

import '../screens/home_screen.dart';

abstract final class AppRoutes {
  static const home = '/';

  static Map<String, WidgetBuilder> get routes => {
        home: (_) => const HomeScreen(),
      };
}
