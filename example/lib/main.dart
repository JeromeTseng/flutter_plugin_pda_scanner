import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pda_scanner/pda_scanner.dart';
import 'package:pda_scanner/scan_gun.dart';
import 'package:pda_scanner_example/pages/device_info_page.dart';
import 'package:pda_scanner_example/pages/device_log_page.dart';
import 'package:pda_scanner_example/pages/home_page.dart';

void main(){
  PdaScanner.initScanner();
  runApp(const MyApp(title: "PDA扫码示例"));
}

class MyApp extends StatelessWidget {
  final String _title;

  const MyApp({super.key, title}) : _title = title ?? 'Flutter Demo';

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      title: _title,
      defaultTransition: Transition.rightToLeft,
      transitionDuration: const Duration(milliseconds: 200),
      getPages: [
        GetPage(name: HomePage.routeName, page: () =>  const HomePage()),
        GetPage(name: DeviceInfoPage.routeName, page: () => const DeviceInfoPage()),
        GetPage(name: DeviceLogPage.routeName, page: ()=> const DeviceLogPage())
      ],
      initialRoute: HomePage.routeName,
    );
  }
}
