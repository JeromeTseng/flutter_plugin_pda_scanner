import 'dart:developer';
import 'dart:ffi';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'pda_scanner_platform_interface.dart';

class MethodChannelPdaScanner extends PdaScannerPlatform {


  // 扫码触发的回调函数
  static final Map<String,Callback> _callback = {};
  static final String LOG_KEY = "PDAScanner_log";

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
              if(kDebugMode){
                log("tag: $tag\t接收到条码内容: $barcodeContent\t${DateTime.now()}");
              }
              callback.call(barcodeContent);
            });
          }
          break;
        case 'sendLogToFlutter':
          if(kDebugMode){
            log(call.arguments);
          }
          // 获取到sharedpreference实例
          final SharedPreferences prefs = await SharedPreferences.getInstance();
          // 获取到日志列表
          var logList = prefs.getStringList(LOG_KEY);
          // 为空则创建新数组
          logList??=[];
          // 添加
          logList.add(call.arguments);
          // 持久化
          prefs.setStringList(LOG_KEY, logList);
      }
      return null;
    });
  }

  @override
  void initScanner()async{
    // 每次初始化扫码器之前都先把日志给清空
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.remove(LOG_KEY);
    await methodChannel.invokeMethod<String>('initScanner');
  }

  @override
  Future<List<Map<String, dynamic>>> getPDAInitLogs() async{
    try{
      // 获取到sharedpreference实例
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      // 获取到日志列表
      return (prefs.getStringList(LOG_KEY)??[])
          .map((e){
        var array = e.split("###&&&***");
        if(array.length == 3){
          return <String,dynamic>{
            "type" : array[0],
            "time" : array[1],
            "content" : array[2]
          };
        }
        return <String,dynamic>{};
      }
      ).where((element)=>element.length == 3).toList();
    }catch(e){
      return [{
        "type":"error",
        "time":DateTime.now(),
        "content": "$e"
      }];
    }
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
      final model = await methodChannel.invokeMethod('getPDAModel');
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

}
