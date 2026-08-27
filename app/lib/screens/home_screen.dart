import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/app_theme.dart';
import '../data/mock_data.dart';
import '../providers/catalog_provider.dart';
import '../providers/cart_provider.dart';
import '../widgets/product_card.dart';
import 'all_categories_screen.dart';
import 'category_screen.dart';
import 'search_screen.dart';
import 'cart_screen.dart';
import 'orders_screen.dart';
import 'profile_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});
  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int index = 0;
  @override
  Widget build(BuildContext c) {
    final cart = c.watch<CartProvider>();
    final pages = [
      const _HomeTab(),
      const AllCategoriesScreen(),
      const OrdersScreen(),
      const CartScreen(),
      const ProfileScreen()
    ];
    return Scaffold(
        body: pages[index],
        bottomNavigationBar: NavigationBar(
            selectedIndex: index,
            onDestinationSelected: (i) => setState(() => index = i),
            destinations: [
              const NavigationDestination(
                  icon: Icon(Icons.home_outlined),
                  selectedIcon: Icon(Icons.home),
                  label: 'Home'),
              const NavigationDestination(
                  icon: Icon(Icons.grid_view_outlined),
                  selectedIcon: Icon(Icons.grid_view),
                  label: 'Categories'),
              const NavigationDestination(
                  icon: Icon(Icons.receipt_long_outlined),
                  selectedIcon: Icon(Icons.receipt_long),
                  label: 'Orders'),
              NavigationDestination(
                  icon: Badge(
                      isLabelVisible: cart.count > 0,
                      label: Text('${cart.count}'),
                      child: const Icon(Icons.shopping_bag_outlined)),
                  selectedIcon: const Icon(Icons.shopping_bag),
                  label: 'Cart'),
              const NavigationDestination(
                  icon: Icon(Icons.person_outline),
                  selectedIcon: Icon(Icons.person),
                  label: 'Profile')
            ]));
  }
}

class _HomeTab extends StatelessWidget {
  const _HomeTab();
  @override
  Widget build(BuildContext c) {
    final cat = c.watch<CatalogProvider>();
    final cart = c.watch<CartProvider>();
    final sections = <Map<String, String>>[
      {'title': 'Fresh Groceries', 'category': 'Grocery'},
      {'title': 'Fruits & Vegetables', 'category': 'Fruits'},
      {'title': 'Meat & Non-Veg', 'category': 'Meat'},
      {'title': 'Baby Care', 'category': 'Baby Products'},
      {'title': 'Fashion', 'category': 'Men'},
      {'title': 'Electronics', 'category': 'Electronics'},
      {'title': 'Home & Furniture', 'category': 'Furniture'},
      {'title': 'Best Sellers', 'category': ''},
      {'title': 'Deals & Offers', 'category': ''},
      {'title': 'New Arrivals', 'category': ''},
      {'title': 'Recommended', 'category': ''},
    ];
    return SafeArea(
        child: CustomScrollView(slivers: [
      SliverPadding(
          padding: const EdgeInsets.fromLTRB(18, 18, 18, 8),
          sliver: SliverToBoxAdapter(
              child: Row(children: [
            Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                    color: const Color(0xffe7f6ee),
                    borderRadius: BorderRadius.circular(12)),
                child: const Icon(Icons.shopping_bag_rounded,
                    color: AppTheme.green)),
            const SizedBox(width: 10),
            const Expanded(
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                  Text('ZeptoPluse',
                      style: TextStyle(
                          color: AppTheme.green,
                          fontWeight: FontWeight.w900,
                          fontSize: 16)),
                  SizedBox(height: 2),
                  Text('Deliver to',
                      style: TextStyle(color: Colors.grey, fontSize: 12)),
                  SizedBox(height: 2),
                  Row(children: [
                    Icon(Icons.location_on, color: AppTheme.green, size: 18),
                    SizedBox(width: 4),
                    Text('Hyderabad',
                        style: TextStyle(
                            fontWeight: FontWeight.w900, fontSize: 18))
                  ])
                ])),
            Badge(
                isLabelVisible: cart.count > 0,
                label: Text('${cart.count}'),
                child: IconButton(
                    onPressed: () => Navigator.push(c,
                        MaterialPageRoute(builder: (_) => const CartScreen())),
                    icon: const Icon(Icons.shopping_bag_outlined, size: 27)))
          ]))),
      SliverPadding(
          padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 8),
          sliver: SliverToBoxAdapter(
              child: InkWell(
                  onTap: () => Navigator.push(c,
                      MaterialPageRoute(builder: (_) => const SearchScreen())),
                  borderRadius: BorderRadius.circular(15),
                  child: Container(
                      height: 52,
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(15)),
                      child: const Row(children: [
                        Icon(Icons.search, color: Colors.grey),
                        SizedBox(width: 10),
                        Text('Search groceries, clothes, electronics...',
                            style: TextStyle(color: Colors.grey))
                      ]))))),
      SliverPadding(
          padding: const EdgeInsets.fromLTRB(18, 12, 18, 0),
          sliver: SliverToBoxAdapter(
              child: Container(
                  height: 154,
                  padding: const EdgeInsets.all(22),
                  decoration: BoxDecoration(
                      color: const Color(0xffdff3e9),
                      borderRadius: BorderRadius.circular(24)),
                  child: Row(children: [
                    const Expanded(
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                          Text('Everything you need,',
                              style: TextStyle(
                                  fontSize: 21, fontWeight: FontWeight.w900)),
                          Text('nearby.',
                              style: TextStyle(
                                  fontSize: 21,
                                  fontWeight: FontWeight.w900,
                                  color: AppTheme.green)),
                          SizedBox(height: 8),
                          Text('Groceries • Fashion • Electronics',
                              style: TextStyle(color: Colors.black54)),
                        ])),
                    Container(
                        width: 104,
                        height: 104,
                        decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(22)),
                        child: const Icon(Icons.local_mall_rounded,
                            size: 58, color: AppTheme.green))
                  ])))),
      SliverPadding(
          padding: const EdgeInsets.fromLTRB(18, 24, 18, 10),
          sliver: SliverToBoxAdapter(
              child: Row(children: [
            const Text('Shop by Category',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.w900)),
            const Spacer(),
            TextButton(
                onPressed: () => Navigator.push(
                    c,
                    MaterialPageRoute(
                        builder: (_) => const AllCategoriesScreen())),
                child: const Text('View all'))
          ]))),
      SliverPadding(
        padding: const EdgeInsets.symmetric(horizontal: 18),
        sliver: SliverToBoxAdapter(
            child: SizedBox(
                height: 112,
                child: ListView.separated(
                    scrollDirection: Axis.horizontal,
                    itemCount: categories.take(8).length,
                    separatorBuilder: (_, __) => const SizedBox(width: 10),
                    itemBuilder: (_, i) {
                      final x = categories[i];
                      return InkWell(
                          onTap: () => Navigator.push(
                              c,
                              MaterialPageRoute(
                                  builder: (_) =>
                                      CategoryScreen(category: x['name']!))),
                          child: Container(
                              width: 82,
                              padding: const EdgeInsets.all(8),
                              decoration: BoxDecoration(
                                  color: Colors.white,
                                  borderRadius: BorderRadius.circular(16)),
                              child: Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Expanded(
                                        child: Image.asset(x['image']!,
                                            fit: BoxFit.contain)),
                                    const SizedBox(height: 5),
                                    Text(x['name']!,
                                        maxLines: 1,
                                        overflow: TextOverflow.ellipsis,
                                        style: const TextStyle(
                                            fontSize: 11,
                                            fontWeight: FontWeight.w700))
                                  ])));
                    }))),
      ),
      if (cat.loading)
        const SliverToBoxAdapter(
            child: Padding(
                padding: EdgeInsets.all(30),
                child: Center(child: CircularProgressIndicator())))
      else
        ...sections.map((s) {
          final list = s['category']!.isEmpty
              ? cat.products
              : cat.search('', category: s['category']!);
          final products = list.take(10).toList();
          return SliverPadding(
              padding: const EdgeInsets.fromLTRB(18, 22, 18, 0),
              sliver: SliverToBoxAdapter(
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                    Text(s['title']!,
                        style: const TextStyle(
                            fontSize: 20, fontWeight: FontWeight.w900)),
                    const SizedBox(height: 10),
                    SizedBox(
                        height: 286,
                        child: ListView.separated(
                            scrollDirection: Axis.horizontal,
                            itemBuilder: (_, i) => SizedBox(
                                width: 176,
                                child: ProductCard(product: products[i])),
                            separatorBuilder: (_, __) =>
                                const SizedBox(width: 12),
                            itemCount: products.length))
                  ])));
        }),
      const SliverToBoxAdapter(child: SizedBox(height: 30))
    ]));
  }
}
