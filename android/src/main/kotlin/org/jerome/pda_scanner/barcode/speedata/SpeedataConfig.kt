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
            "T60"
        )

        /**
         * 判断是否为 Speedata 类型的设备
         * @author 曾兴顺  2024/01/16
         */
        fun isThisDevice(): Boolean {
            // 设备型号名称
            val modelName = Build.MODEL
            // 设备产品名称
            val productName = Build.PRODUCT
           return SUPPORTED_DEVICE.any {
                return it.contains(modelName) || it.contains(productName)
                        || modelName.contains(it) || productName.contains(it)
            }
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
                    this@SpeedataConfig.emitErrorMessageTrace(ex)
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

    override fun emitErrorMessageTrace(exception: Exception?) {
        if(exception != null){
            methodChannel.invokeMethod(ERROR_EMITTER_METHOD, exception.toString())
        }
    }

    override fun emitErrorMessage(errorMessage: String?) {
        if(errorMessage != null){
            methodChannel.invokeMethod(ERROR_EMITTER_METHOD, errorMessage)
        }
    }


}