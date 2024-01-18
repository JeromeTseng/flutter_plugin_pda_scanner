import 'package:bruno/bruno.dart';
import 'package:flutter/material.dart';
import 'package:getwidget/components/avatar/gf_avatar.dart';
import 'package:getwidget/components/list_tile/gf_list_tile.dart';
import 'package:pda_scanner/pda_scanner.dart';
import 'package:pda_scanner_example/pages/device_info_page.dart';
import 'package:pda_scanner_example/pages/home_page.dart';

void main() => runApp(const MyApp(title: "PDA扫码示例"));

class MyApp extends StatelessWidget {
  final String _title;

  const MyApp({super.key, title}) : _title = title ?? 'Flutter Demo';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: _title,
      routes: {
        HomePage.routeName: (_) => const HomePage(),
        DeviceInfo.routeName: (_) => const DeviceInfo(),
      },
      initialRoute: HomePage.routeName,
    );
  }
}
