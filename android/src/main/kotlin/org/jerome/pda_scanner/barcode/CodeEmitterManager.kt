package org.jerome.pda_scanner.barcode

import android.content.Context
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.hikivision.HikvisionConfig
import org.jerome.pda_scanner.barcode.invengo.InvengoConfig
import org.jerome.pda_scanner.barcode.speedata.SpeedataConfig
import org.jerome.pda_scanner.barcode.zebra.ZebraConfig

/**
 * 扫码管理器
 * @author 曾兴顺  2024/01/16
 */
abstract class CodeEmitterManager {

    companion object {
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

    fun sendLogMessage(methodChannel: MethodChannel, logDesc: String) {
        methodChannel.invokeMethod(CODE_LOG_METHOD, logDesc)
    }

}