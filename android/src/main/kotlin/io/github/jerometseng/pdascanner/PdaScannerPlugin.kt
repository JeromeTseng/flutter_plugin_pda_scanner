package io.github.jerometseng.pdascanner

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager.Companion.CODE_EMITTER_CHANNEL
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager.Companion.GET_PDA_MODEL
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager.Companion.INIT_SCANNER
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager.Companion.IS_PDA_SUPPORTED
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager.Companion.LOG_TAG
import io.github.jerometseng.pdascanner.util.NotificationUtil


/**
 * PDA 扫码插件
 * @author 曾兴顺
 */
class PdaScannerPlugin : FlutterPlugin, ActivityAware {

    private var methodChannel: MethodChannel? = null
    private var notificationUtil: NotificationUtil? = null

    // 扫码触发管理器
    private var codeEmitterManager: CodeEmitterManager? = null
    private var binaryMessenger: BinaryMessenger? = null

    // 是否进行了初始化操作
    private var initFlag: Boolean = false

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
                when (methodCall.method) {
                    IS_PDA_SUPPORTED -> result.success(CodeEmitterManager.isPDASupported())
                    GET_PDA_MODEL -> result.success(Build.MODEL)
                    "getPlatformVersion" -> result.success("Android ${Build.VERSION.RELEASE}")
                    INIT_SCANNER -> result.success(initScanner())
                    "navigateToSystemHome" -> navigateToSystemHome()
                    "errorSound" -> notificationUtil?.errorSound()
                }
            }
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
            notificationUtil = NotificationUtil()
            Log.i(LOG_TAG, "onAttachedToActivity")
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
    private fun initScanner():Boolean {
        if (initFlag) {
            return true
        }
        if (activity?.applicationContext != null) {
            // 初始化扫码管理器
            return try {
                codeEmitterManager = CodeEmitterManager.initCodeEmitterManager(
                    activity!!,
                    methodChannel!!
                )
                // 开启扫码器
                codeEmitterManager?.open()
                initFlag = true
                true
            } catch (ex: Throwable) {
                Log.e(LOG_TAG, "初始化扫码器出错：$ex")
                false
            }
        } else {
            Log.e(LOG_TAG, "activity对象为空！")
            return false
        }
    }

    /**
     * 返回系统桌面 而不退出程序
     * @author 曾兴顺
     */
    private fun navigateToSystemHome() {
        // 创建Intent对象
        val intent = Intent()
        // 设置Intent动作
        intent.setAction(Intent.ACTION_MAIN)
        // 设置Intent种类
        intent.addCategory(Intent.CATEGORY_HOME)
        //标记
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity?.startActivity(intent)
    }

}
