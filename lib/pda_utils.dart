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
  static const _methodChannel =
      MethodChannel("io.github.jerometseng/pda_scanner");

  // 扫码成功音频资源
  static final AudioPlayer _scanSuccessAudioPlayer = AudioPlayer();

  // 扫码失败音频资源
  static final AudioPlayer _scanFailureAudioPlayer = AudioPlayer();

  // PDA是否进行过初始化
  static bool _initFlag = false;

  // 日志标签
  static const String logTag = "PDA_SCANNER#";

  // 扫码触发的回调函数
  static final Map<String, Callback> _callback = {};

  // 日志集合
  static final List<InitLogModel> _logList = [];

  /// 初始化PDA扫码枪
  static Future<void> init() async {
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
      log('PDA扫码器初始化完成', name: logTag);
    }
    // 设置音频
    _loadScanAudioPlayer();
  }

  /// 自定义初始化扫码枪
  /// action: 广播行为
  /// label: 获取数据的标签
  static Future<void> initByCustom(
    String action,
    String label, {
    PdaDataType dataType = PdaDataType.STRING,
  }) async {
    WidgetsFlutterBinding.ensureInitialized();
    if (!Platform.isAndroid) {
      throw Exception(['PDA插件只支持安卓系统设备！']);
    }
    // 设置通道回调
    _setMethodCallback();
    // 调用安卓初始化PDA方法 标记PDA已进行过初始化
    bool? initSuccess = await _methodChannel.invokeMethod<bool>(
      'initScannerCustom',
      {
        'action': action,
        'label': label,
        'dataType':dataType.name
      },
    );
    if (initSuccess ?? false) {
      _initFlag = true;
      log('PDA扫码器初始化完成', name: logTag);
    }
    // 设置音频
    _loadScanAudioPlayer();
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

  /// 检测是否调用过初始化方法
  static void _checkIsInit() {
    if (!_initFlag) {
      throw Exception([
        '请在合适的时机使用\nPdaUtils.init()\t或\nPdaUtils.initByCustom(action,label)\n方法初始化 pda_scanner 插件！'
      ]);
    }
  }

  /// 设置音频
  static void _loadScanAudioPlayer() async {
    // 设置成功音频资源
    var scanSuccessAudioResource = await rootBundle
        .load('packages/pda_scanner/assets/audio/scan_success.wav');
    _scanSuccessAudioPlayer.setReleaseMode(ReleaseMode.stop);
    _scanSuccessAudioPlayer
        .setSource(BytesSource(scanSuccessAudioResource.buffer.asUint8List()));
    // 设置失败音频资源
    var scanFailureAudioResource = await rootBundle
        .load('packages/pda_scanner/assets/audio/scan_failure.wav');
    _scanFailureAudioPlayer.setReleaseMode(ReleaseMode.stop);
    _scanFailureAudioPlayer
        .setSource(BytesSource(scanFailureAudioResource.buffer.asUint8List()));
    log('扫码音频资源加载成功！', name: logTag);
  }

  /// 查询该PDA是否支持扫码
  static Future<bool> isThisPDASupported() async {
    _checkIsInit();
    return await _methodChannel.invokeMethod('isPDASupported') ?? false;
  }

  /// 获取PDA设备型号
  static Future<String> getPDAModel() async {
    _checkIsInit();
    return await _methodChannel.invokeMethod('getPDAModel') ?? '未知';
  }

  /// 获取安卓系统版本
  static Future<String> getPlatformVersion() async {
    _checkIsInit();
    return await _methodChannel.invokeMethod<String>('getPlatformVersion') ??
        '未知系统版本';
  }

  /// 订阅扫码事件
  static void on(String tag, Callback emitterCallback) {
    _checkIsInit();
    _callback[tag] = emitterCallback;
  }

  /// 根据tag取消扫码订阅
  static void off(String tag) {
    _checkIsInit();
    _callback.remove(tag);
    log("取消监听tag: $tag", name: logTag);
  }

  /// 取消全部扫码订阅
  static void offAll() {
    _checkIsInit();
    _callback.clear();
    log('取消所有tag监听', name: logTag);
  }

  /// 嘟嘟错误提示音
  static void errorSoundDudu() {
    _checkIsInit();
    _methodChannel.invokeMethod('errorSound');
  }

  /// 扫码成功的人声
  /// 考虑到扫码频率快 如果使用单例audioplayer则必须等待上一次音频播放完成才能播放下一个
  /// 导致音频播放节奏跟不上扫码速度 不太符合实际场景 故每次播放都新建一个audioplayer
  static void successSoundHumanVoice() async {
    _checkIsInit();
    await _scanSuccessAudioPlayer.resume();
  }

  /// 扫码失败的人声
  /// 考虑到扫码频率快 如果使用单例audioplayer则必须等待上一次音频播放完成才能播放下一个
  /// 导致音频播放节奏跟不上扫码速度 不太符合实际场景 故每次播放都新建一个audioplayer
  static void errorSoundHumanVoice({bool playErrorSoundDudu = true}) async {
    _checkIsInit();
    await _scanFailureAudioPlayer.resume();
    if (playErrorSoundDudu) {
      errorSoundDudu();
    }
  }

  /// 获取订阅的tag列表
  static List<String> getOnTagList() {
    _checkIsInit();
    return _callback.keys.toList();
  }

  /// 获取初始化日志
  static List<InitLogModel> getInitLogList() {
    _checkIsInit();
    if (_logList.isEmpty) {
      log('日志列表为空！', name: logTag);
    }
    return _logList;
  }

  /// 返回系统桌面
  static void navigateToSystemHome() {
    _checkIsInit();
    _methodChannel.invokeMethod('navigateToSystemHome');
  }

  /// 关闭扫码器
  static void closeScanner() {
    _methodChannel.invokeMethod('closeScanner');
  }
}

/// 日志实体类
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

enum PdaDataType { STRING, BYTE_ARRAY }
