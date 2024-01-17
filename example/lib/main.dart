import 'dart:developer';

import 'package:bruno/bruno.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:pda_scanner/pda_scanner.dart';

void main() {
  runApp(const MaterialApp(
    debugShowCheckedModeBanner: false,
    home: MyScaffold(),
  ));
}

class MyScaffold extends StatefulWidget {
  const MyScaffold({super.key});

  @override
  State<MyScaffold> createState() => _MyScaffoldState();
}

class _MyScaffoldState extends State<MyScaffold> {
  String _platformVersion = 'Unknown';
  String _pdaModel = 'Unknown';
  bool _isSupported = false;
  String _barcode = '';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    String pdaModel;
    bool isSupported;
    try {
      platformVersion =
          await PdaScanner.getPlatformVersion() ?? 'Unknown platform version';
      pdaModel = await PdaScanner.getPDAModel() ?? 'UnKnown model';
      isSupported = await PdaScanner.isThisPDASupported();
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
      pdaModel = 'Failed to get model';
      isSupported = false;
    }
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
      _pdaModel = pdaModel;
      _isSupported = isSupported;
    });
  }

  @override
  Widget build(BuildContext context) {
    PdaScanner.on((barcode) {
      setState(() {
        this._barcode = barcode;
      });
    });
    return Scaffold(
      appBar: AppBar(
        title: const Text('PDA示例'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('安卓平台版本: $_platformVersion'),
            SizedBox(height: 10,),
            Text('设备型号: $_pdaModel'),
            SizedBox(height: 10,),
            Text('该设备是否支持扫码: ${_isSupported?'支持':'不支持'}'),
            SizedBox(height: 8,),
            Text("条码内容：$_barcode")
          ],
        ),
      ),
    );
  }
}
