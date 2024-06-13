import 'dart:developer';
import 'dart:io';

import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

// 扫码回调的参数
typedef Callback = void Function(String barcode);

/// PDA工具类
abstract class PdaUtils {
  // 方法通道
  static const _methodChannel = MethodChannel("org.jerome/pda_scanner");

  // 扫码成功音频资源
  static ByteData? _scanSuccessAudioResource;

  // 扫码失败音频资源
  static ByteData? _scanFailureAudioResource;

  // PDA是否进行过初始化
  static bool _initFlag = false;

  // 日志标签
  static const String logTag = "PdaUtils";

  // 扫码触发的回调函数
  static final Map<String, Callback> _callback = {};

  // 日志集合
  static final List<InitLogModel> _logList = [];

  static ByteData? resource;

  /// 初始化PDA扫码枪
  static void init() async {
    WidgetsFlutterBinding.ensureInitialized();
    if (!Platform.isAndroid) {
      throw Exception(['PDA插件只支持安卓系统设备！']);
    }
    if (_initFlag) {
      log('PdaUtils已经进行过初始化操作。', name: logTag);
      return;
    }
    // 设置通道回调
    _setMethodCallback();
    // 调用安卓初始化PDA方法 标记PDA已进行过初始化
    bool? initSuccess = await _methodChannel.invokeMethod<bool>('initScanner');
    if (initSuccess ?? false) {
      _initFlag = true;
      log('PDA扫码器初始化结束',name: logTag);
    }
    // 设置音频
    loadScanAudioPlayer();
  }

  /// 设置通道回调
  static void _setMethodCallback() {
    _methodChannel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'sendBarcodeToFlutter':
          // 获取到的条码内容
          String? barcodeContent = call.arguments;
          if (barcodeContent != null) {
            // 开发模式下打印接受条码的内容
            if (kDebugMode) {
              log("接收到条码内容: $barcodeContent\t时间：${DateTime.now()}",
                  name: logTag);
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

  /// 设置音频
  static void loadScanAudioPlayer() async {
    // 设置成功音频资源
    _scanSuccessAudioResource = await rootBundle
        .load('packages/pda_scanner/assets/audio/scan_success.wav');
    // 设置失败音频资源
    _scanFailureAudioResource = await rootBundle
        .load('packages/pda_scanner/assets/audio/scan_failure.wav');
    log('扫码音频资源加载成功！', name: logTag);
  }

  // 查询该PDA是否支持扫码
  static Future<bool> isThisPDASupported() async {
    return await _methodChannel.invokeMethod('isPDASupported') ?? false;
  }

  // 获取PDA设备型号
  static Future<String> getPDAModel() async {
    return await _methodChannel.invokeMethod('getPDAModel') ?? '未知';
  }

  static Future<String> getPlatformVersion() async {
    return await _methodChannel.invokeMethod<String>('getPlatformVersion') ??
        '未知系统版本';
  }

  /// 订阅扫码事件
  static void on(String tag, Callback emitterCallback) {
    _callback[tag] = emitterCallback;
  }

  /// 根据tag取消扫码订阅
  static void off(String tag) {
    _callback.remove(tag);
    log("取消监听tag: $tag", name: logTag);
  }

  /// 取消全部扫码订阅
  static void offAll() {
    _callback.clear();
    log('取消所有tag监听', name: logTag);
  }

  /// 嘟嘟错误提示音
  static void errorSoundDudu() {
    _methodChannel.invokeMethod('errorSound');
  }

  /// 扫码成功的人声
  /// 考虑到扫码频率快 如果使用单例audioplayer则必须等待上一次音频播放完成才能播放下一个
  /// 导致音频播放节奏跟不上扫码速度 不太符合实际场景 故每次播放都新建一个audioplayer
  static void successSoundHumanVoice() async {
    if (_scanSuccessAudioResource == null) {
      log('音频资源未加载成功！', name: logTag);
      return;
    }
    var audioPlayer = AudioPlayer();
    await audioPlayer
        .play(BytesSource(_scanSuccessAudioResource!.buffer.asUint8List()));
    // 延迟1.5s丢弃资源
    Future.delayed(const Duration(seconds: 1, milliseconds: 500), () {
      audioPlayer.dispose();
    });
  }

  /// 扫码失败的人声
  /// 考虑到扫码频率快 如果使用单例audioplayer则必须等待上一次音频播放完成才能播放下一个
  /// 导致音频播放节奏跟不上扫码速度 不太符合实际场景 故每次播放都新建一个audioplayer
  static void errorSoundHumanVoice({bool playErrorSoundDudu = true}) async {
    if (_scanFailureAudioResource == null) {
      log('音频资源未加载成功！', name: logTag);
      return;
    }
    var audioPlayer = AudioPlayer();
    await audioPlayer
        .play(BytesSource(_scanFailureAudioResource!.buffer.asUint8List()));
    if (playErrorSoundDudu) {
      errorSoundDudu();
    }
    // 延迟1.5s丢弃资源
    Future.delayed(const Duration(seconds: 1, milliseconds: 500), () {
      audioPlayer.dispose();
    });
  }

  /// 获取订阅的tag列表
  static List<String> getOnTagList() {
    return _callback.keys.toList();
  }

  /// 获取初始化日志
  static List<InitLogModel> getInitLogList() {
    if (_logList.isEmpty) {
      log('日志列表为空！', name: logTag);
    }
    return _logList;
  }

  /// 返回系统桌面
  static void navigateToSystemHome() {
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
