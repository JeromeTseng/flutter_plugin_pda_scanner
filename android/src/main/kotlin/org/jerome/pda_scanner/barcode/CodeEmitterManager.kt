package org.jerome.pda_scanner.barcode

import android.content.Context
import android.os.Build
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.speedata.SpeedataConfig
import org.jerome.pda_scanner.barcode.zebra.ZebraConfig

/**
 * 扫码管理器
 * @author 曾兴顺  2024/01/16
 */
abstract class CodeEmitterManager {

    companion object{
        // 与 flutter 通信的管道
        const val CODE_EMITTER_CHANNEL = "org.jerome/pda_scanner"
        // 与 flutte 通信的方法 发送接收到的条码数据
        const val CODE_EMITTER_METHOD = "sendBarcodeToFlutter"
        // 与 flutte 通信的方法 发送错误
        const val ERROR_EMITTER_METHOD = "sendERRORToFlutter"
        // 查询 PDA 是否支持扫码
        private const val IS_PDA_SUPPORTED = "isPDASupported"
        // 查询设备型号
        private const val GET_PDA_MODEL = "getPDAMoodel"

        /**
         * 初始化扫描管理器
         * @author 曾兴顺  2024/01/16
         */
        fun initCodeEmitterManager(
            context: Context,
            methodChannel: MethodChannel,
        ): CodeEmitterManager? {
            // 设置 flutter 界面调用的方法
            setMethodCall(methodChannel)
            // 初始化扫码器
            try{
                if(SpeedataConfig.isThisDevice()){
                    return SpeedataConfig(context,methodChannel)
                }
            }catch (ex:Exception){
                Log.i("SPEEDATA","思必拓(SPEEDATA)扫码设备初始化失败。")
            }

            try {
                if(ZebraConfig.isThisDevice()){
                    return ZebraConfig(context,methodChannel)
                }
            }catch (ex:Exception){
                Log.i("ZEBRA","斑马(ZEBRA)扫码设备初始化失败。")
            }
            return null
        }

        /**
         * 设置 flutter 界面调用的方法 即 flutter 调用android
         * @author 曾兴顺  2024/01/16
         */
        private fun setMethodCall(methodChannel : MethodChannel){
            methodChannel.setMethodCallHandler { call, result ->
                when(call.method){
                    IS_PDA_SUPPORTED -> result.success(isPDASupported())
                    GET_PDA_MODEL -> result.success(Build.MODEL)
                    "getPlatformVersion" -> result.success("Android ${Build.VERSION.RELEASE}")
                }
            }
        }

        /**
         * PDA是否支持扫码：
         * SpeedataConfig.isThisDevice() => 是否为 Speedata 设备 ？
         * ZebraConfig.isThisDevice() => 是否为 Zebra 设备 ？
         * @author 曾兴顺  2024/01/16
         */
        private fun isPDASupported() : Boolean{
            return SpeedataConfig.isThisDevice() || ZebraConfig.isThisDevice()
        }

    }

    // 打开扫码器 一开始调用
    abstract fun open()

    // 暂时关闭 如后台运行程序
    abstract fun detach()

    // 重新打开 如将后台运行的程序唤醒
    abstract fun reConnect()

    // 关闭扫码器 释放资源
    abstract fun close()

    // 发送错误信息
    fun emitErrorMessageTrace(methodChannel: MethodChannel?,exception: Exception?){
        if(exception != null){
            methodChannel?.invokeMethod(ERROR_EMITTER_METHOD, exception.toString())
        }
    }

    fun emitErrorMessage(methodChannel: MethodChannel?,errorMessage: String?){
        if(errorMessage != null){
            methodChannel?.invokeMethod(ERROR_EMITTER_METHOD, errorMessage)
        }
    }

}