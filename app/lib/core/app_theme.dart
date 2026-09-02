import 'package:flutter/material.dart';

class AppTheme {
  static const green = Color(0xFF3454D1);
  static const dark = Color(0xFF182451);
  static const bg = Color(0xFFF6F7FC);
  static ThemeData get theme => ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor: bg,
        colorScheme: ColorScheme.fromSeed(seedColor: green),
        fontFamily: 'Roboto',
        appBarTheme: const AppBarTheme(
            backgroundColor: bg,
            elevation: 0,
            surfaceTintColor: Colors.transparent),
        cardTheme: CardThemeData(
            elevation: 0,
            color: Colors.white,
            margin: EdgeInsets.zero,
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(18))),
        inputDecorationTheme: InputDecorationTheme(
          filled: true,
          fillColor: Colors.white,
          contentPadding:
              const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
          border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(14),
              borderSide: BorderSide.none),
          enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(14),
              borderSide: BorderSide.none),
          focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(14),
              borderSide: const BorderSide(color: green, width: 1.2)),
        ),
      );
}
