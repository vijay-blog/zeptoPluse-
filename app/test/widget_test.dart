import 'package:flutter_test/flutter_test.dart';
import 'package:zeptopluse_customer/main.dart';

void main() {
  testWidgets('renders splash app name', (WidgetTester tester) async {
    await tester.pumpWidget(const ZeptoPluseApp());
    expect(find.text('ZeptoPluse'), findsOneWidget);
    await tester.pumpAndSettle(const Duration(milliseconds: 1200));
  });
}
