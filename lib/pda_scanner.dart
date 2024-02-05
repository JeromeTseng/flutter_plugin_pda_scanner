
import 'dart:developer';

import 'package:pda_scanner/scan_gun.dart';

import 'pda_scanner_platform_interface.dart';

class PdaScanner {

  // 初始化扫码器
  static Future<void> initScanner({bool initScanGun = true}) async {
    try{
      if(initScanGun){
        // 初始化扫码枪扫码
        TextInputBinding();
      }
    }catch(e){
      log("初始化蓝牙扫码器出错！$e",name: "PdaScanner");
    }
    try{
      // 初始化PDA扫码
      PdaScannerPlatform.instance.initScanner();
    }catch(e){
      log("初始化PDA扫码器出错！$e",name: "PdaScanner");
    }
  }

  // 获取安卓版本
  static Future<String?> getPlatformVersion() {
    return PdaScannerPlatform.instance.getPlatformVersion();
  }

  // 查询该PDA是否支持扫码
  static Future<bool> isThisPDASupported() {
    return PdaScannerPlatform.instance.isThisPDASupported();
  }

  // 获取PDA设备型号
  static Future<String?> getPDAModel() async {
    return PdaScannerPlatform.instance.getPDAModel();
  }

  // 监听函数 以tag进行区分
  static void on(String tag, Callback callback) {
    PdaScannerPlatform.instance.on(tag, callback);
  }

  // 监听函数 以tag进行区分
  static void off(String tag) {
    PdaScannerPlatform.instance.off(tag);
  }

  // 获取扫码器初始化日志
  static Future<List<Map<String,dynamic>>> getPDAInitLog() async{
    return PdaScannerPlatform.instance.getPDAInitLogs();
  }

  // 错误提示音 做一个简单的同步 其实这里是有问题的
  static void errorSound() {
    PdaScannerPlatform.instance.errorSound();
  }

}
