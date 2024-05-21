package org.jerome.pda_scanner

import android.app.Activity
import android.app.Application
import android.os.Build
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
import org.jerome.pda_scanner.barcode.CodeEmitterManager.Companion.GET_PDA_MODEL
import org.jerome.pda_scanner.barcode.CodeEmitterManager.Companion.INIT_SCANNER
import org.jerome.pda_scanner.barcode.CodeEmitterManager.Companion.IS_PDA_SUPPORTED
import org.jerome.pda_scanner.barcode.CodeEmitterManager.Companion.LOG_TAG
import org.jerome.pda_scanner.util.NotificationUtil

class PdaScannerPlugin : FlutterPlugin, ActivityAware {

    private var methodChannel: MethodChannel? = null
    private var notificationUtil:NotificationUtil? = null

    // 扫码触发管理器
    private var codeEmitterManager: CodeEmitterManager? = null
    private var binaryMessenger: BinaryMessenger? = null

    // 是否进行了初始化操作
    private var initFlag:Boolean = false

    // Activity
    private var activity: Activity? = null

    /**
     * 连接上flutter引擎
     * @author 曾兴顺  2024/01/17
     */
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        try {

            binaryMessenger = flutterPluginBinding.binaryMessenger
            // 设置通道 与 flutter ui 进行通信
            methodChannel =
                MethodChannel(flutterPluginBinding.binaryMessenger, CODE_EMITTER_CHANNEL)
            methodChannel?.setMethodCallHandler { methodCall, result ->
                when(methodCall.method){
                    IS_PDA_SUPPORTED -> result.success(CodeEmitterManager.isPDASupported())
                    GET_PDA_MODEL -> result.success(Build.MODEL)
                    "getPlatformVersion" -> result.success("Android ${Build.VERSION.RELEASE}")
                    INIT_SCANNER -> initScanner()
                    "errorSound" -> notificationUtil?.errorSound()
                }
            }


            (flutterPluginBinding.applicationContext as Application).registerActivityLifecycleCallbacks(
                object : Application.ActivityLifecycleCallbacks {
                    override fun onActivityCreated(
                        activity: Activity,
                        savedInstanceState: Bundle?
                    ) {
                    }

                    override fun onActivityStarted(activity: Activity) {

                    }

                    // 当程序被唤醒 前台运行时 重新打开触发扫码的事件
                    override fun onActivityResumed(activity: Activity) {
                        try {
                            codeEmitterManager?.reConnect()
                        } catch (ex: Exception) {
                            Log.e("onActivityResumed", ex.toString())
                        }
                    }

                    // 当程序后台运行时 暂时关闭扫码器触发的扫码事件 避免误操作
                    override fun onActivityPaused(activity: Activity) {
                        try {
                            codeEmitterManager?.detach()
                        } catch (ex: Exception) {
                            Log.e("onActivityPaused", ex.toString())
                        }
                    }

                    override fun onActivityStopped(activity: Activity) {
                    }

                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                    }

                })
        } catch (ex: Exception) {
            Log.e("pda_scanner", ex.toString())
        }

    }

    /**
     * 连接到activity后设置activity实例
     * @author 曾兴顺  2024/01/16
     */
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        try {
            activity = binding.activity as FlutterActivity
            notificationUtil = NotificationUtil(binding.activity)
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "onAttachedToActivity: $ex")
        }
    }

    /**
     * 断开引擎
     * @author 曾兴顺  2024/01/17
     */
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel?.setMethodCallHandler(null)
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
        try {
            // 关闭扫码管理器
            codeEmitterManager?.close()
            Log.i(LOG_TAG, "onDetachedFromActivity方法：已断开与Activity的连接...")
            activity = null
        } catch (ex: Exception) {
            Log.e(LOG_TAG, ex.toString())
        }
    }

    // 初始化扫码器
    private fun initScanner(){
        if(initFlag){
            return
        }
        if (activity!!.applicationContext != null) {
            // 初始化扫码管理器
            try {
                codeEmitterManager = CodeEmitterManager.initCodeEmitterManager(
                    activity!!,
                    methodChannel!!
                )
                // 开启扫码器
                codeEmitterManager?.open()
                initFlag = true
            }catch (ex:Exception){
                Log.e(LOG_TAG, "初始化扫码器出错：$ex" )
            }
        }
    }

}
