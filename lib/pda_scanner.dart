import 'pda_scanner_platform_interface.dart';

class PdaScanner {

  // 初始化扫码器
  static Future<void> initScanner() async {
    PdaScannerPlatform.instance.initScanner();
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
}
