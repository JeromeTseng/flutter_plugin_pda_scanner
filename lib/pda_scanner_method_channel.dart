import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'pda_scanner_platform_interface.dart';

class MethodChannelPdaScanner extends PdaScannerPlatform {

  @visibleForTesting
  final methodChannel = const MethodChannel("org.jerome/pda_scanner");

  MethodChannelPdaScanner(){
    methodChannel.setMethodCallHandler((call) async{
      switch(call.method){
        case 'sendBarcodeToFlutter':
          if(this.emitterCallback != null){
            // 获取到的条码内容
            String? barcodeContent = call.arguments;
            emitterCallback!(barcodeContent??'');
          }
          break;
      }
      return null;
    });
  }

  // 扫码触发的回调函数
  late Callback? emitterCallback;

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

  // 传入扫码回调的函数
  @override
  void on(Callback emitterCallback) {
    this.emitterCallback = emitterCallback;
  }

  //
  @override
  void off() {
    this.emitterCallback = null;
  }
}
