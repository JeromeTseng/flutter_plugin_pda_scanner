
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'pda_scanner_method_channel.dart';

// 扫码回调的参数
typedef Callback = void Function(String barcode);

abstract class PdaScannerPlatform extends PlatformInterface {

  PdaScannerPlatform() : super(token: _token);

  static final Object _token = Object();

  static PdaScannerPlatform _instance = MethodChannelPdaScanner();

  static PdaScannerPlatform get instance => _instance;


  static set instance(PdaScannerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  void init(){

  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() 方法未实现！');
  }

  Future<bool> isThisPDASupported(){
    throw UnimplementedError('isThisPDASupported() 方法未实现！');
  }

  Future<String?> getPDAModel(){
    throw UnimplementedError('getPDAModel() 方法未实现！');
  }

  void on(Callback emitterCallback){
    throw UnimplementedError('on 方法未实现！');
  }

  void off(){
    throw UnimplementedError('off() 方法未实现！');
  }

}
