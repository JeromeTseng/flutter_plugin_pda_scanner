import 'package:bruno/bruno.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:getwidget/getwidget.dart';
import 'package:pda_scanner/pda_utils.dart';
import 'package:pda_scanner_example/pages/device_info_page.dart';

class HomePage extends StatelessWidget {
  static const String routeName = "/HomePage";

  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvoked: (_) {
        PdaUtils.navigateToSystemHome();
      },
      child: SafeArea(
        bottom: true,
        child: Scaffold(
          appBar: buildHomeAppBar(),
          body: const HomeBody(),
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
  var actionController = TextEditingController()
    ..text = "android.intent.action.BARCODEDATA";
  var labelController = TextEditingController()..text = "barcode_result";
  GetStorage? box;
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
    return Container(
      color: Colors.white,
      child: Column(
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
              height: 220,
              child: Row(
                children: [
                  Expanded(
                    child: ListView(
                      children: [
                        buildAndroidVersion(), // 安卓版本
                        buildModelName(), // 设备型号
                        buildScanSupported(), // 是否支持扫码
                        const SizedBox(
                          height: 2.5,
                        ),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 5.0),
                          child: BrnSmallMainButton(
                            title: '手动设置PDA',
                            bgColor: Colors.blueGrey[900],
                            onTap: () => {
                              BrnDialogManager.showConfirmDialog(
                                context,
                                barrierDismissible: false,
                                title: "手动设置PDA",
                                cancel: '取消',
                                confirm: '确定',
                                messageWidget: Column(
                                  children: [
                                    const Text(
                                      '请注意大小写',
                                      style: TextStyle(
                                          color: Colors.red,
                                          fontWeight: FontWeight.bold),
                                    ),
                                    BrnTextInputFormItem(
                                      controller: actionController,
                                      isRequire: true,
                                      isEdit: true,
                                      title: " action：",
                                      hint: "请输入广播地址",
                                    ),
                                    BrnTextInputFormItem(
                                      controller: labelController,
                                      isRequire: true,
                                      isEdit: true,
                                      title: " label：",
                                      hint: "请输入数据标签",
                                    ),
                                  ],
                                ),
                                onConfirm: () async {
                                  var action = actionController.text;
                                  var label = labelController.text;
                                  if (action.isNotEmpty && label.isNotEmpty) {
                                    box?.write("action", action);
                                    box?.write("label", label);
                                    PdaUtils.initByCustom(action, label);
                                    Get.back();
                                  } else {
                                    BrnToast.show(
                                        "action 或 label 设置有误！", context);
                                  }
                                },
                                onCancel: Get.back,
                              )
                            },
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 5.0),
                      child: SingleChildScrollView(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            BrnSmallMainButton(
                              title: '警告提示音',
                              bgColor: Colors.blueGrey[900],
                              onTap: PdaUtils.errorSoundDudu,
                            ),
                            const SizedBox(height: 3),
                            BrnSmallMainButton(
                              title: '扫码成功提示音',
                              bgColor: Colors.blueGrey[900],
                              onTap: PdaUtils.successSoundHumanVoice,
                            ),
                            const SizedBox(height: 3),
                            BrnSmallMainButton(
                              title: '扫码失败提示音',
                              bgColor: Colors.blueGrey[900],
                              onTap: PdaUtils.errorSoundHumanVoice,
                            ),
                            const SizedBox(height: 3),
                            BrnSmallMainButton(
                              title: '初始化日志',
                              bgColor: Colors.blueGrey[900],
                              onTap: () => {
                                showDialog(
                                  context: context,
                                  barrierDismissible: true,
                                  builder: (_) {
                                    return BrnScrollableTextDialog(
                                      title: "PDA初始化日志",
                                      contentText: PdaUtils.getInitLogList()
                                          .join("")
                                          .replaceFirst("\n", ""),
                                      isShowOperateWidget: false,
                                    );
                                  },
                                )
                              },
                            ),
                            const SizedBox(height: 3),
                            BrnSmallMainButton(
                              title: '监听的tag',
                              bgColor: Colors.blueGrey[900],
                              onTap: () => {
                                showDialog(
                                  context: context,
                                  barrierDismissible: true,
                                  builder: (_) {
                                    return BrnScrollableTextDialog(
                                      title: "监听的tag",
                                      contentText:
                                          PdaUtils.getOnTagList().join("\n"),
                                      isShowOperateWidget: false,
                                    );
                                  },
                                )
                              },
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const Expanded(
            child: BarcodeListView(),
          )
        ],
      ),
    );
  }

  // 安卓版本
  Widget buildAndroidVersion() {
    return GFListTile(
      margin: const EdgeInsets.symmetric(vertical: 0, horizontal: 0),
      avatar: GFAvatar(
        size: 20,
        backgroundColor: Colors.blueGrey[900],
        child: const Icon(
          Icons.android,
          color: Colors.white,
          size: 20,
        ),
      ),
      title: _buildTitle('安卓版本'),
      subTitle: _buildSubTitle(_androidVersion),
    );
  }

  //设备型号
  Widget buildModelName() {
    return GFListTile(
      onTap: () {
        Get.toNamed(DeviceInfoPage.routeName);
      },
      margin: const EdgeInsets.symmetric(vertical: 0, horizontal: 0),
      avatar: GFAvatar(
        size: 20,
        backgroundColor: Colors.blueGrey[900],
        child: const Icon(
          Icons.phone_android_rounded,
          color: Colors.white,
          size: 20,
        ),
      ),
      title: _buildTitle('设备型号'),
      subTitle: _buildSubTitle(_modelName),
    );
  }

  // 是否支持扫码
  Widget buildScanSupported() {
    return GFListTile(
      margin: const EdgeInsets.symmetric(vertical: 0, horizontal: 0),
      avatar: GFAvatar(
        size: 20,
        backgroundColor: Colors.blueGrey[900],
        child: const Icon(
          Icons.document_scanner_rounded,
          color: Colors.white,
          size: 20,
        ),
      ),
      title: _buildTitle('支持扫码？'),
      subTitle: _buildSubTitle(
          (_isScanSupported != null && _isScanSupported!) ? '支持' : '未知，请测试。'),
    );
  }

  Future<void> initEquipmentInfo() async {
    String androidVersion = await PdaUtils.getPlatformVersion();
    String modelName = await PdaUtils.getPDAModel();
    bool isScanSupported = await PdaUtils.isThisPDASupported();
    await GetStorage.init();
    box = GetStorage();
    actionController.text = (box?.read("action")) ?? '';
    labelController.text = (box?.read("label")) ?? '';
    setState(() {
      _androidVersion = androidVersion;
      _modelName = modelName;
      _isScanSupported = isScanSupported;
    });
  }

  Widget _buildTitle(String content) {
    return Text(
      content,
      overflow: TextOverflow.ellipsis,
      maxLines: 1,
    );
  }

  Widget _buildSubTitle(String? content) {
    return Text(
      content ?? '',
      overflow: TextOverflow.ellipsis,
      maxLines: 1,
      style: const TextStyle(
        color: Color(0xFF595959),
      ),
    );
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
        ? FittedBox(
            child: BrnAbnormalStateWidget(
              title: '', // 给个空字符串撑开距离
              content: '目前没有扫描过的条码',
            ),
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

  void addCode(String barcode) {
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
