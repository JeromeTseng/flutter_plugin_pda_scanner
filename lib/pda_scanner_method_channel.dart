import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'pda_scanner_platform_interface.dart';

class MethodChannelPdaScanner extends PdaScannerPlatform {

  bool _logTrigger = false;

  // 扫码触发的回调函数
  static final Map<String,Callback> _callback = {};

  @visibleForTesting
  final methodChannel = const MethodChannel("org.jerome/pda_scanner");

  MethodChannelPdaScanner(){
    methodChannel.setMethodCallHandler((call) async{
      switch(call.method){
        case 'sendBarcodeToFlutter':
          // 获取到的条码内容
          String? barcodeContent = call.arguments;
          if(barcodeContent != null){
            // 遍历所有回调函数 并进行调用
            _callback.forEach((tag, callback) {
              if(_logTrigger){
                log("tag: $tag    接收到条码内容: $barcodeContent");
              }
              callback.call(barcodeContent);
            });
          }
          break;
      }
      return null;
    });
  }

  // 获取安卓版本
  @override
  Future<String?> getPlatformVersion() async {
    try{
      final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
      return version;
    }catch(e){
      return e.toString();
    }
  }

  // 该PDA是否支持扫码
  @override
  Future<bool> isThisPDASupported() async{
    try{
      final isSupported = await methodChannel.invokeMethod('isPDASupported');
      return isSupported ?? false;
    }catch(e){
      return false;
    }
  }

  // 获取PDA的型号信息 以 [/] 分隔
  @override
  Future<String?> getPDAModel() async {
    try{
      final model = await methodChannel.invokeMethod('getPDAMoodel');
      return model ?? "null";
    }catch(e){
      return e.toString();
    }
  }

  // 传入Tag 传入扫码回调的函数
  @override
  void on(String tag,Callback emitterCallback) {
    _callback[tag] = emitterCallback;
  }

  // 取消对tag上的监听
  @override
  void off(String tag) {
    _callback.remove(tag);
  }

  // 打开日志
  @override
  void openLog() {
    _logTrigger = true;
  }

  @override
  void closeLog() {
    _logTrigger = false;
  }
}
