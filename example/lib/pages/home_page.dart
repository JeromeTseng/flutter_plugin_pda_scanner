import 'package:get/get.dart';
import 'package:bruno/bruno.dart';
import 'package:flutter/material.dart';
import 'package:getwidget/getwidget.dart';
import 'package:pda_scanner/pda_scanner.dart';
import 'package:pda_scanner_example/model/device_log.dart';
import 'package:pda_scanner_example/pages/device_info_page.dart';
import 'package:pda_scanner_example/pages/device_log_page.dart';

class HomePage extends StatelessWidget {
  static const String routeName = "/HomePage";

  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    PdaScanner.initScanner();
    PdaScanner.initScanner();
    return Scaffold(
      appBar: buildHomeAppBar(),
      body: const HomeBody(),
      floatingActionButton: FloatingActionButton(
        backgroundColor: Colors.blueGrey[900],
        onPressed: () {
          Get.toNamed(DeviceInfoPage.routeName);
        },
        child: const Icon(
          Icons.fingerprint_rounded,
          color: Colors.white,
          size: 40,
        ),
      ),
    );
  }

  PreferredSize buildHomeAppBar() {
    return BrnAppBar(
      leading: const SizedBox(),
      themeData: BrnAppBarConfig.dark(),
      //文本title
      title: 'PDA扫码示例',
      actions: [
        IconButton(onPressed: (){
          Get.toNamed(DeviceLogPage.routeName);
        }, icon: const Icon(Icons.error,color: Colors.white,))
      ],
    );
  }
}

class HomeBody extends StatefulWidget {
  const HomeBody({super.key});

  @override
  State<HomeBody> createState() => _HomeBodyState();
}

class _HomeBodyState extends State<HomeBody> {
  String? _androidVersion = "unknown";
  String? _modelName = "unknown";
  bool? _isScanSupported = false;

  @override
  void initState() {
    super.initState();
    initEquipmentInfo();
    PdaScanner.on(HomePage.routeName, (barcode) {
      // 展示条码
      Get.closeAllSnackbars();
      Get.snackbar(
        '接收到条码信息',
        barcode,
        backgroundColor: Colors.white,
        messageText: Text(
          barcode,
          style: const TextStyle(
            color: Colors.red,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        BrnNoticeBar(
          leftWidget: const Icon(
            Icons.notifications_active_outlined,
            color: Color(0xFFFF7F00),
          ),
          content: '请进行扫码看是否会弹窗显示条码内容',
          noticeStyle: NoticeStyles.runningWithArrow,
          showRightIcon: false,
          backgroundColor: const Color(0xFFFDFBEC),
          textColor: const Color(0xFFFF7F00),
        ),
        Expanded(
          child: SizedBox(
            width: 250,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                buildAndroidVersion(), // 安卓版本
                buildModelName(), // 设备型号
                buildScanSupported(), // 是否支持扫码
              ],
            ),
          ),
        ),
      ],
    );
  }

  // 安卓版本
  Widget buildAndroidVersion() {
    return GFListTile(
        avatar: GFAvatar(
          size: 32,
          backgroundColor: Colors.blueGrey[900],
          child: const Icon(
            Icons.android,
            color: Colors.white,
            size: 27,
          ),
        ),
        titleText: '安卓版本',
        subTitleText: _androidVersion);
  }

  //设备型号
  Widget buildModelName() {
    return GFListTile(
      avatar: GFAvatar(
        size: 32,
        backgroundColor: Colors.blueGrey[900],
        child: const Icon(
          Icons.phone_android_rounded,
          color: Colors.white,
          size: 28,
        ),
      ),
      titleText: '设备型号',
      subTitleText: _modelName,
    );
  }

  // 是否支持扫码
  Widget buildScanSupported() {
    return GFListTile(
      avatar: GFAvatar(
        size: 32,
        backgroundColor: Colors.blueGrey[900],
        child: const Icon(
          Icons.document_scanner_rounded,
          color: Colors.white,
          size: 26,
        ),
      ),
      titleText: '是否支持扫码',
      subTitleText:
          (_isScanSupported != null && _isScanSupported!) ? '支持' : '未知，请测试。',
    );
  }

  Future<void> initEquipmentInfo() async {
    String? androidVersion = await PdaScanner.getPlatformVersion();
    String? modelName = await PdaScanner.getPDAModel();
    bool isScanSupported = await PdaScanner.isThisPDASupported();
    setState(() {
      _androidVersion = androidVersion;
      _modelName = modelName;
      _isScanSupported = isScanSupported;
    });
  }
}
