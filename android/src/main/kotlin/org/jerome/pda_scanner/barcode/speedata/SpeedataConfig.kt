package org.jerome.pda_scanner.barcode.speedata

import android.content.Context
import android.os.Build
import com.scandecode.ScanDecode
import com.scandecode.inf.ScanInterface.OnScanListener
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager

class SpeedataConfig(
    // 上下文
    private var context: Context,
    // 与flutter通信的通道
    private var methodChannel: MethodChannel

) : CodeEmitterManager() {
    // 是否要触发扫码
    var flag : Boolean = false

    // 静态方法
    companion object {
        // 支持 Speedata（思必拓）扫码的设备列表
        private val SUPPORTED_DEVICE: MutableList<String> = mutableListOf(
            "SC40G", "FG40-4G/5G", "SC55G", "FG50-4G/5G", "HT35", "SD60", "FG60-4G/5G", "ST55G",
            "FG50RT-4G/5G", "ST55RT", "FG60RT-4G/5G", "KT50-UHF", "SD50RT", "T60","T50","FG40",
            "FG50RT","SC40G","SC55G"
        )

        /**
         * 判断是否为 Speedata 类型的设备
         * @author 曾兴顺  2024/01/16
         */
        fun isThisDevice(): Boolean {
            // 设备型号名称
            val modelName = Build.MODEL.uppercase()
           return SUPPORTED_DEVICE.find { it == modelName } != null
        }

    }

    // Speedata扫码器
    private lateinit var scanCode: ScanDecode

    override fun open() {
        this.scanCode = ScanDecode(this.context)
        this.scanCode.initService("true")
        this.flag = true
        this.scanCode.getBarCode(object : OnScanListener {
            // 监听到扫码回调
            override fun getBarcode(barcode: String?) {
                try {
                    if (barcode != null && this@SpeedataConfig.flag) {
                        methodChannel.invokeMethod(CODE_EMITTER_METHOD, barcode)
                    }
                }catch (ex:Exception){
                    this@SpeedataConfig.emitErrorMessageTrace(methodChannel,ex)
                }
            }

            override fun getBarcodeByte(p0: ByteArray?) {
            }

        })
    }

    override fun detach() {
        this.flag = false
    }

    override fun reConnect() {
        this.flag = true
    }

    override fun close() {
        this.scanCode.stopScan()
    }


}