package io.github.jerometseng.pdascanner.pda_type

import android.content.Context
import android.util.Log
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.ScannerResults
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.github.jerometseng.pdascanner.pda_type.hikivision.HikvisionConfig
import io.github.jerometseng.pdascanner.pda_type.invengo.InvengoConfig
import io.github.jerometseng.pdascanner.pda_type.seuic.Cruise5GConfig
import io.github.jerometseng.pdascanner.pda_type.speedata.SpeedataConfig
import io.github.jerometseng.pdascanner.pda_type.zebra.ZebraConfig
import java.util.Date

/**
 * 扫码管理器
 * @author 曾兴顺  2024/01/16
 */
abstract class CodeEmitterManager(private val context: Context,private val methodChannel: MethodChannel) {

    companion object {
        // 与 flutter 通信的管道
        const val CODE_EMITTER_CHANNEL = "org.jerome/pda_scanner"

        // 与 flutter 通信的方法 发送接收到的条码数据
        const val CODE_EMITTER_METHOD = "sendBarcodeToFlutter"

        // 与 flutter 通信的方法 发送日志
        const val CODE_LOG_METHOD = "sendLogToFlutter"

        // 查询 PDA 是否支持扫码
        const val IS_PDA_SUPPORTED = "isPDASupported"

        // 查询系统版本
        const val GET_PLATFORM_VERSION = "getPlatformVersion"

        // 查询设备型号
        const val GET_PDA_MODEL = "getPDAModel"

        // 初始化扫码器
        const val INIT_SCANNER = "initScanner"

        // 自定义广播初始化
        const val INIT_SCANNER_CUSTOM = "initScannerCustom"

        // 手动关闭
        const val CLOSE_SCANNER = "closeScanner"

        // 正常返回系统桌面 不退出程序
        const val NAVIGATE_TO_SYSTEM_HOME = "navigateToSystemHome"

        // 错误提示音
        const val ERROR_SOUND = "errorSound"

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
            return if (DeviceDetect.isSpeedataDevice()) {
                // 思必拓扫码器
                SpeedataConfig(context, methodChannel)
            } else if (DeviceDetect.isZebraDevice()) {
                // 斑马扫码器
                ZebraConfig(context, methodChannel)
            } else if (DeviceDetect.isInvengoDevice()) {
                // 远望谷扫码器
                InvengoConfig(context, methodChannel)
            } else if (DeviceDetect.isHikvisionDevice()) {
                // 海康威视扫码器
                HikvisionConfig(context, methodChannel)
            } else if(DeviceDetect.isSeuicCruise5G()){
                // 东集酷路泽扫码器
                Cruise5GConfig(context,methodChannel)
            } else {
                null
            }
        }


        /**
         * PDA是否支持扫码：
         * @author 曾兴顺  2024/01/16
         */
        fun isPDASupported(): Boolean {
            return DeviceDetect.isThisDeviceSupported()
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

    fun logInfo(infoMessage: String) {
        log("info", infoMessage)
    }

    fun logError(infoMessage: String) {
        log("error", infoMessage)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun log(logType: String, infoMessage: String) {
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                sendLogMessage("${logType}###&&&***${Date().time}###&&&***$infoMessage")
            }
        }
        if(logType=="info"){
            Log.i(LOG_TAG,infoMessage)
        }else{
            Log.e(LOG_TAG,infoMessage)
        }
    }

    // 发送初始化日志给flutter
    fun sendLogMessage(logDesc: String) {
        methodChannel.invokeMethod(CODE_LOG_METHOD, logDesc)
    }

}