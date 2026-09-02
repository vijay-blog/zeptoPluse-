import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'core/app_theme.dart';
import 'providers/cart_provider.dart';
import 'providers/catalog_provider.dart';
import 'providers/order_provider.dart';
import 'providers/address_provider.dart';
import 'screens/splash_screen.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final prefs = await SharedPreferences.getInstance();
  if (!prefs.containsKey('nm.guestCustomerId')) {
    await prefs.setString(
      'nm.guestCustomerId',
      'guest_${DateTime.now().millisecondsSinceEpoch}',
    );
  }
  runApp(const NexaMartApp());
}

class NexaMartApp extends StatelessWidget {
  const NexaMartApp({super.key});
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => CatalogProvider()..load()),
        ChangeNotifierProvider(create: (_) => CartProvider()),
        ChangeNotifierProvider(create: (_) => OrderProvider()),
        ChangeNotifierProvider(create: (_) => AddressProvider()),
      ],
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        title: 'NexaMart',
        theme: AppTheme.theme,
        home: const SplashScreen(),
      ),
    );
  }
}
