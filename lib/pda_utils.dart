import 'dart:developer';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

// 扫码回调的参数
typedef Callback = void Function(String barcode);

/// PDA工具类
class PdaUtils {
  // 方法通道
  final _methodChannel = const MethodChannel("org.jerome/pda_scanner");

  // PDA是否进行过初始化
  bool _initFlag = false;

  // 日志标签
  final String logTag = "PdaUtils";

  // 扫码触发的回调函数
  static final Map<String, Callback> _callback = {};

  // 日志集合
  final List<InitLogModel> _logList = [];

  /// 私有化构造器
  PdaUtils._() {
    WidgetsFlutterBinding.ensureInitialized();
    _methodChannel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'sendBarcodeToFlutter':
          // 获取到的条码内容
          String? barcodeContent = call.arguments;
          if (barcodeContent != null) {
            // 开发模式下打印接受条码的内容
            if (kDebugMode) {
              log("接收到条码内容: $barcodeContent\t时间：${DateTime.now()}", name: logTag);
            }
            // 遍历所有回调函数 并进行调用
            _callback.forEach((tag, callback) {
              callback.call(barcodeContent);
            });
          }
          break;
        case 'sendLogToFlutter':
          // 添加
          List<String> logs = (call.arguments as String).split('###&&&***');
          if (logs.length == 3) {
            _logList.add(InitLogModel(
              type: logs[0],
              time: int.tryParse(logs[1]),
              content: logs[2],
            ));
          }
          break;
      }
      return null;
    });
  }

  /// PDA实例
  static PdaUtils? _instance;

  /// 获取单例
  static PdaUtils instance() {
    if(!Platform.isAndroid){
      throw Exception(['PDA插件只支持安卓系统设备！']);
    }
    _instance ??= PdaUtils._();
    return _instance!;
  }

  /// 初始化PDA扫码枪
  void init() async {
    if (_initFlag) {
      log('PdaUtils已经进行过初始化操作。', name: logTag);
      return;
    }
    await _methodChannel.invokeMethod<String>('initScanner');

    /// 标记PDA已进行过初始化
    _initFlag = true;
  }

  // 查询该PDA是否支持扫码
  Future<bool> isThisPDASupported() async {
    return await _methodChannel.invokeMethod('isPDASupported') ?? false;
  }

  // 获取PDA设备型号
  Future<String> getPDAModel() async {
    return await _methodChannel.invokeMethod('getPDAModel') ?? '未知';
  }

  Future<String> getPlatformVersion() async {
    return await _methodChannel.invokeMethod<String>('getPlatformVersion') ??
        '未知系统版本';
  }

  /// 订阅扫码事件
  void on(String tag, Callback emitterCallback) {
    _callback[tag] = emitterCallback;
  }

  /// 根据tag取消扫码订阅
  void off(String tag) {
    _callback.remove(tag);
  }

  /// 取消全部扫码订阅
  void offAll() {
    _callback.clear();
  }

  /// 错误提示音
  void errorSound() {
    _methodChannel.invokeMethod('errorSound');
  }

  /// 获取订阅的tag列表
  List<String> getOnTagList() {
    return _callback.keys.toList();
  }

  /// 获取初始化日志
  List<InitLogModel> getInitLogList() {
    return _logList;
  }

  /// 返回系统桌面
  void navigateToSystemHome(){
    _methodChannel.invokeMethod('navigateToSystemHome');
  }
}

class InitLogModel {
  InitLogModel({
    String? type,
    int? time,
    String? content,
  }) {
    _type = type;
    _time = time;
    _content = content;
  }

  String? _type;
  int? _time;
  String? _content;

  @override
  String toString() {
    return '\n类型：$_type\n内容：$_content\n时间：${DateTime.fromMillisecondsSinceEpoch(_time ?? 0)}';
  }
}
