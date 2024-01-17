
import 'pda_scanner_platform_interface.dart';

class PdaScanner {
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

  static void on (Callback callback){
    PdaScannerPlatform.instance.on(callback);
  }

  static void off(){
    PdaScannerPlatform.instance.off();
  }

}
