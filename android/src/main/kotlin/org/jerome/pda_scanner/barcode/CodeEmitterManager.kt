package org.jerome.pda_scanner.barcode

import android.content.Context
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.invengo.InvengoConfig
import org.jerome.pda_scanner.barcode.speedata.SpeedataConfig
import org.jerome.pda_scanner.barcode.zebra.ZebraBroadcastReceiver
import org.jerome.pda_scanner.barcode.zebra.ZebraConfig
import org.jerome.pda_scanner.barcode.zebra.ZebraIntentConfig

/**
 * 扫码管理器
 * @author 曾兴顺  2024/01/16
 */
abstract class CodeEmitterManager {

    companion object{
        // 与 flutter 通信的管道
        const val CODE_EMITTER_CHANNEL = "org.jerome/pda_scanner"
        // 与 flutter 通信的方法 发送接收到的条码数据
        const val CODE_EMITTER_METHOD = "sendBarcodeToFlutter"
        // 与 flutter 通信的方法 发送日志
        const val CODE_LOG_METHOD = "sendLogToFlutter"
        // 查询 PDA 是否支持扫码
        const val IS_PDA_SUPPORTED = "isPDASupported"
        // 查询设备型号
        const val GET_PDA_MODEL = "getPDAModel"
        // 初始化扫码器
        const val INIT_SCANNER = "initScanner"
        // 设置日志标识
        const val LOG_TAG = "JEROME#"

        /**
         * 初始化扫描管理器
         * @author 曾兴顺  2024/01/16
         */
        fun initCodeEmitterManager(
            context: Context,
            methodChannel: MethodChannel,
        ): CodeEmitterManager? {
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
                    return ZebraIntentConfig(context,methodChannel)
                }
            }catch (ex:Exception){
                Log.i("ZEBRA","斑马(ZEBRA)扫码设备初始化失败。")
            }
            return null
        }


        /**
         * PDA是否支持扫码：
         * SpeedataConfig.isThisDevice() => 是否为 Speedata 设备 ？
         * ZebraConfig.isThisDevice() => 是否为 Zebra 设备 ？
         * @author 曾兴顺  2024/01/16
         */
        fun isPDASupported() : Boolean{
            return SpeedataConfig.isThisDevice() || ZebraConfig.isThisDevice()
                    || InvengoConfig.isThisDevice()
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

    fun sendLogMessage(methodChannel: MethodChannel,logDesc:String){
        methodChannel.invokeMethod(CODE_LOG_METHOD,logDesc)
    }

}