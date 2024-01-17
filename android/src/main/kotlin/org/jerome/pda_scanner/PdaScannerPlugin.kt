package org.jerome.pda_scanner


import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager
import org.jerome.pda_scanner.barcode.CodeEmitterManager.Companion.CODE_EMITTER_CHANNEL

class PdaScannerPlugin: FlutterPlugin,ActivityAware {

  private lateinit var methodChannel : MethodChannel
  // 扫码触发管理器
  private var codeEmitterManager: CodeEmitterManager? = null
  private var binaryMessenger: BinaryMessenger? = null
  // Activity
  private var activity : Activity? = null

  /**
   * 连接上flutter引擎
   * @author 曾兴顺  2024/01/17
   */
  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    try {
      binaryMessenger = flutterPluginBinding.binaryMessenger
      // 设置通道 与 flutter ui 进行通信
      methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, CODE_EMITTER_CHANNEL)

      (flutterPluginBinding.applicationContext as Application).registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        // 当程序被唤醒 前台运行时 重新打开触发扫码的事件
        override fun onActivityResumed(activity: Activity) {
          codeEmitterManager?.reConnect()
        }

        // 当程序后台运行时 暂时关闭扫码器触发的扫码事件 避免误操作
        override fun onActivityPaused(activity: Activity) {
          codeEmitterManager?.detach()
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

      })
    }catch (ex:Exception){
      Log.e("pda_scanner",ex.toString())
    }

  }

  /**
   * 连接到activity后设置activity实例
   * @author 曾兴顺  2024/01/16
   */
  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity as FlutterActivity
    // 1、设置扫码管理器
    if(activity!!.applicationContext != null){
      // 初始化扫码管理器
      codeEmitterManager = CodeEmitterManager.initCodeEmitterManager(
        activity!!.applicationContext,
        methodChannel
      )
      // 开启扫码器
      codeEmitterManager?.open()
    }
  }

  /**
   * 断开引擎
   * @author 曾兴顺  2024/01/17
   */
  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    this.onDetachedFromActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    this.onAttachedToActivity(binding)
  }

  /**
   * 检测到activity断开
   * @author 曾兴顺  2024/01/16
   */
  override fun onDetachedFromActivity() {
    // 关闭扫码管理器
    codeEmitterManager?.close()
    activity = null;
  }

}
