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
import org.jerome.pda_scanner.barcode.chainway.ChainwayConfig
import org.jerome.pda_scanner.barcode.hikivision.HikvisionConfig
import org.jerome.pda_scanner.barcode.zebra.ZebraIntentConfig

class PdaScannerPlugin : FlutterPlugin, ActivityAware {

    private lateinit var methodChannel: MethodChannel

    // 是否初始化过扫码器
    private var initFlag = false

    // 扫码触发管理器
    private var codeEmitterManager: CodeEmitterManager? = null

    // 存放广播触发管理器的集合
    private var codeEmitterManagerList: MutableList<CodeEmitterManager> = mutableListOf()
    private var binaryMessenger: BinaryMessenger? = null


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

            methodChannel.setMethodCallHandler { methodCall, result ->
                when(methodCall.method){
                    IS_PDA_SUPPORTED -> result.success(CodeEmitterManager.isPDASupported())
                    GET_PDA_MODEL -> result.success(Build.MODEL)
                    "getPlatformVersion" -> result.success("Android ${Build.VERSION.RELEASE}")
                    INIT_SCANNER -> initScanner()
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
                            if (codeEmitterManagerList.isNotEmpty()) {
                                codeEmitterManagerList.forEach {
                                    it.reConnect()
                                }
                            }
                        } catch (ex: Exception) {
                            Log.e("onActivityResumed", ex.toString())
                        }
                    }

                    // 当程序后台运行时 暂时关闭扫码器触发的扫码事件 避免误操作
                    override fun onActivityPaused(activity: Activity) {
                        try {
                            codeEmitterManager?.detach()
                            if (codeEmitterManagerList.isNotEmpty()) {
                                codeEmitterManagerList.forEach {
                                    it.detach()
                                }
                            }
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
            // 1、设置扫码管理器
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "onAttachedToActivity: $ex")
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
        try {
            // 关闭扫码管理器
            codeEmitterManager?.close()
            if (codeEmitterManagerList.isNotEmpty()) {
                codeEmitterManagerList.forEach {
                    it.close()
                }
            }
            Log.i(LOG_TAG, "onDetachedFromActivity方法：已断开与Activity的连接...")
            activity = null
        } catch (ex: Exception) {
            Log.e(LOG_TAG, ex.toString())
        }
    }

    // 初始化扫码器
    private fun initScanner(){
        if (activity!!.applicationContext != null) {
            if(initFlag) return
            // 初始化扫码管理器
            try {
                codeEmitterManager = CodeEmitterManager.initCodeEmitterManager(
                    activity!!,
                    methodChannel
                )
                // 默认把斑马的广播Intent Action加上 如果斑马native scan未生效 可以增加配置文件扫码
                codeEmitterManagerList.add(
                    ZebraIntentConfig(
                        activity!!.applicationContext,
                        methodChannel
                    )
                )
                // 开启扫码器
                codeEmitterManager?.open()
            }catch (ex:Exception){
                Log.e(LOG_TAG, "初始化ZEBRA/SPEEDATA扫码器出错：$ex" )
            }
            // 如果扫码器为空 则代表不是斑马或思必拓的扫码器
            try{
                if (codeEmitterManager == null) {
                    // 添加海康威视广播接受条码
                    codeEmitterManagerList.add(
                        HikvisionConfig(
                            activity!!.applicationContext,
                            methodChannel
                        )
                    )
                    // 增加chainway广播接收扫码
                    codeEmitterManagerList.add(
                        ChainwayConfig(
                            activity!!.applicationContext,
                            methodChannel
                        )
                    )
                }
                if(codeEmitterManagerList.isNotEmpty()){
                    codeEmitterManagerList.forEach {
                        it.open()
                    }
                }
                initFlag = true
            }catch (ex:Exception){
                Log.e(LOG_TAG,"初始化广播扫码器时出错：$ex")
            }
        }
    }

}
