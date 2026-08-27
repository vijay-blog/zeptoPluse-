class Product {
  final int id;
  final String name;
  final String description;
  final String brand;
  final String categoryId;
  final String categoryName;
  final List<String> images;
  final double mrp;
  final double sellingPrice;
  final double discountPercentage;
  final String unit;
  final String weight;
  final String size;
  final Map<String, String> attributes;
  final bool available;
  final int stockQuantity;
  final String deliveryType;

  const Product({
    required this.id,
    required this.name,
    required this.description,
    required this.brand,
    required this.categoryId,
    required this.categoryName,
    required this.images,
    required this.mrp,
    required this.sellingPrice,
    required this.discountPercentage,
    required this.unit,
    required this.weight,
    required this.size,
    required this.attributes,
    required this.available,
    required this.stockQuantity,
    required this.deliveryType,
  });

  String get category => categoryName;
  String get imageAsset => images.isEmpty ? '' : images.first;
  int get stock => stockQuantity;
  double get discount => discountPercentage > 0
      ? discountPercentage
      : (mrp <= 0 ? 0 : ((mrp - sellingPrice) / mrp) * 100);

  factory Product.fromJson(Map<String, dynamic> j) {
    final imageFromServer = (j['imageAsset'] ?? j['imageUrl'] ?? '').toString();
    final imgs = (j['images'] is List)
        ? (j['images'] as List).map((e) => e.toString()).toList()
        : <String>[];
    if (imgs.isEmpty && imageFromServer.isNotEmpty) {
      imgs.add(imageFromServer);
    }
    if (imgs.isEmpty) {
      imgs
        ..clear()
        ..add(_localImageFor((j['name'] ?? '').toString()));
    }
    return Product(
      id: j['id'] is num
          ? (j['id'] as num).toInt()
          : int.tryParse((j['id'] ?? '0').toString()) ?? 0,
      name: (j['name'] ?? '').toString(),
      description: (j['description'] ?? '').toString(),
      brand: (j['brand'] ?? '').toString(),
      categoryId: (j['categoryId'] ?? '').toString(),
      categoryName: (j['categoryName'] ?? j['category'] ?? '').toString(),
      images: imgs,
      mrp: (j['mrp'] ?? 0).toDouble(),
      sellingPrice: (j['sellingPrice'] ?? 0).toDouble(),
      discountPercentage: (j['discountPercentage'] as num? ?? 0).toDouble(),
      unit: (j['unit'] ?? '1 unit').toString(),
      weight: (j['weight'] ?? '').toString(),
      size: (j['size'] ?? '').toString(),
      attributes: (j['attributes'] is Map)
          ? (j['attributes'] as Map)
              .map((k, v) => MapEntry(k.toString(), v.toString()))
          : const <String, String>{},
      available: (j['available'] ?? j['availability'] ?? true) == true,
      stockQuantity: (j['stockQuantity'] as num? ?? 0).toInt(),
      deliveryType: (j['deliveryType'] ?? 'SMALL').toString(),
    );
  }

  static String _localImageFor(String name) {
    final value = name.toLowerCase();
    if (RegExp(r'\bac\b').hasMatch(value)) {
      return 'assets/images/products/ac.png';
    }
    const matches = <String, String>{
      'rice': 'rice',
      'atta': 'atta',
      'flour': 'atta',
      'dal': 'dal',
      'oil': 'oil',
      'apple': 'apple',
      'banana': 'banana',
      'mango': 'mango',
      'grape': 'grapes',
      'tomato': 'tomato',
      'potato': 'potato',
      'onion': 'onion',
      'milk': 'milk',
      'curd': 'curd',
      'paneer': 'paneer',
      'butter': 'butter',
      'egg': 'eggs',
      'chicken': 'chicken',
      'mutton': 'mutton',
      'fish': 'fish',
      'diaper': 'baby_diapers',
      'wipe': 'baby_wipes',
      'lotion': 'baby_lotion',
      't-shirt': 'men_tshirt',
      'dress': 'women_dress',
      'shoe': 'shoes',
      'earbud': 'earbuds',
      'mobile': 'mobile',
      'phone': 'mobile',
      'laptop': 'laptop',
      'tv': 'tv',
      'refrigerator': 'refrigerator',
      'washing': 'washing_machine',
      'sofa': 'sofa',
      'bed': 'bed',
      'table': 'table',
      'chair': 'chair',
    };
    for (final entry in matches.entries) {
      if (value.contains(entry.key)) {
        return 'assets/images/products/${entry.value}.png';
      }
    }
    return 'assets/images/products/rice.png';
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'description': description,
        'brand': brand,
        'categoryId': categoryId,
        'categoryName': categoryName,
        'images': images,
        'mrp': mrp,
        'sellingPrice': sellingPrice,
        'discountPercentage': discountPercentage,
        'unit': unit,
        'weight': weight,
        'size': size,
        'attributes': attributes,
        'available': available,
        'stockQuantity': stockQuantity,
        'deliveryType': deliveryType,
      };
}
