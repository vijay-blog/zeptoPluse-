import 'package:flutter_test/flutter_test.dart';
import 'package:nexamart_customer/main.dart';

void main() {
  testWidgets('renders splash app name', (WidgetTester tester) async {
    await tester.pumpWidget(const NexaMartApp());
    expect(find.text('NexaMart'), findsOneWidget);
    await tester.pumpAndSettle(const Duration(milliseconds: 1200));
  });
}
