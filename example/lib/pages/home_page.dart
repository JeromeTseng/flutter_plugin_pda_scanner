import 'dart:developer';

import 'package:bruno/bruno.dart';
import 'package:flutter/material.dart';
import 'package:getwidget/getwidget.dart';
import 'package:pda_scanner/pda_utils.dart';

class HomePage extends StatelessWidget {
  static const String routeName = "/HomePage";

  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvoked: (_){
        PdaUtils.navigateToSystemHome();
      },
      child: SafeArea(
        bottom: true,
        child: Scaffold(
          appBar: buildHomeAppBar(),
          body: const HomeBody(),
          floatingActionButton: FloatingActionButton(
            backgroundColor: Colors.blueGrey[900],
            onPressed: () {
              PdaUtils.getInitLogList().forEach((element) {
                log('$element');
              });
              PdaUtils.successSoundHumanVoice();
              // 如果是跟路由 跳转页面时取消监听扫码事件 在该回调函数中重新监听事件
              // Get.toNamed(DeviceInfoPage.routeName)?.then((value) {
              //   print("监听到返回首页...");
              // });
            },
            child: const Icon(
              Icons.fingerprint_rounded,
              color: Colors.white,
              size: 40,
            ),
          ),
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
          content: '请扫码看底部是否显示条码内容',
          noticeStyle: NoticeStyles.runningWithArrow,
          showRightIcon: false,
          backgroundColor: const Color(0xFFFDFBEC),
          textColor: const Color(0xFFFF7F00),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 10.0),
          child: SizedBox(
            height: 190,
            child: ListView(
              children: [
                buildAndroidVersion(), // 安卓版本
                buildModelName(), // 设备型号
                buildScanSupported(), // 是否支持扫码
              ],
            ),
          ),
        ),
        const Expanded(
          child: BarcodeListView(),
        )
      ],
    );
  }

  // 安卓版本
  Widget buildAndroidVersion() {
    return GFListTile(
        margin: const EdgeInsets.symmetric(vertical: 0, horizontal: 0),
        avatar: GFAvatar(
          size: 25,
          backgroundColor: Colors.blueGrey[900],
          child: const Icon(
            Icons.android,
            color: Colors.white,
            size: 22,
          ),
        ),
        titleText: '安卓版本',
        subTitleText: _androidVersion);
  }

  //设备型号
  Widget buildModelName() {
    return GFListTile(
      onTap: (){
      },
      margin: const EdgeInsets.symmetric(vertical: 0, horizontal: 0),
      avatar: GFAvatar(
        size: 25,
        backgroundColor: Colors.blueGrey[900],
        child: const Icon(
          Icons.phone_android_rounded,
          color: Colors.white,
          size: 22,
        ),
      ),
      titleText: '设备型号',
      subTitleText: _modelName,
    );
  }

  // 是否支持扫码
  Widget buildScanSupported() {
    return GFListTile(
      onTap: (){
        // PdaUtils.errorSoundDudu();
        PdaUtils.errorSoundHumanVoice();
      },
      margin: const EdgeInsets.symmetric(vertical: 0, horizontal: 0),
      avatar: GFAvatar(
        size: 25,
        backgroundColor: Colors.blueGrey[900],
        child: const Icon(
          Icons.document_scanner_rounded,
          color: Colors.white,
          size: 20,
        ),
      ),
      titleText: '是否支持扫码',
      subTitleText:
          (_isScanSupported != null && _isScanSupported!) ? '支持' : '未知，请测试。',
    );
  }

  Future<void> initEquipmentInfo() async {
    String androidVersion = await PdaUtils.getPlatformVersion();
    String modelName = await PdaUtils.getPDAModel();
    bool isScanSupported = await PdaUtils.isThisPDASupported();
    setState(() {
      _androidVersion = androidVersion;
      _modelName = modelName;
      _isScanSupported = isScanSupported;
    });
  }
}

class BarcodeListView extends StatefulWidget {
  const BarcodeListView({super.key});

  @override
  State<BarcodeListView> createState() => _BarcodeListViewState();
}

class _BarcodeListViewState extends State<BarcodeListView> {
  final List<Map<String, String>> barcodes = [];
  final controller = ScrollController();
  final scanKey = GlobalKey<EditableTextState>();

  @override
  void initState() {
    super.initState();
    // 监听扫码
    listen();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [buildBarcodeList(), buildDeleteAllButton()],
    );
  }

  Widget buildBarcodeList() {
    return barcodes.isEmpty
        ? BrnAbnormalStateWidget(
            title: '', // 给个空字符串撑开距离
            content: '目前没有扫描过的条码',
          )
        : ListView.builder(
            controller: controller,
            itemCount: barcodes.length,
            itemExtent: 70,
            itemBuilder: (ctx, index) {
              Map<String, String> barcode = barcodes[index];
              return ListTile(
                title: Text(
                  "条码：${barcode['barcode']}",
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
                subtitle: Text("扫码时间：${barcode['time']}"),
              );
            },
          );
  }

  // 清空已扫条码的按钮
  Widget buildDeleteAllButton() {
    return Positioned(
      right: 5,
      top: -5,
      child: Visibility(
        visible: barcodes.isNotEmpty,
        child: IconButton(
          onPressed: () {
            setState(() {
              barcodes.clear();
            });
            scanKey.currentState?.requestKeyboard();
          },
          icon: const Icon(
            Icons.delete_forever_outlined,
            color: Colors.red,
          ),
          tooltip: '清空条码',
        ),
      ),
    );
  }

  // 监听扫码事件
  void listen() {
    PdaUtils.on(
      HomePage.routeName,
      (barcode) {
        addCode(barcode);
      },
    );
  }

  void addCode(String barcode){
    // 展示条码
    setState(() {
      barcodes.add({
        'barcode': barcode,
        'time': DateTime.now().toString().split('.')[0]
      });
    });
    Future.delayed(
      const Duration(milliseconds: 120),
          () {
        controller.animateTo(
          controller.position.maxScrollExtent,
          duration: const Duration(milliseconds: 200),
          curve: Curves.ease,
        );
      },
    );
  }
}
