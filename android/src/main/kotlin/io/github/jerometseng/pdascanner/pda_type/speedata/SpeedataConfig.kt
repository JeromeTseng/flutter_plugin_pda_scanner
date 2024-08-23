package io.github.jerometseng.pdascanner.pda_type.speedata

import android.content.Context
import com.scandecode.ScanDecode
import com.scandecode.inf.ScanInterface.OnScanListener
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager

class SpeedataConfig(
    // 上下文
    private val context: Context,
    // 与flutter通信的通道
    private val methodChannel: MethodChannel

) : CodeEmitterManager(methodChannel) {

    private val logTag = "SPEEDATA"

    // 是否要触发扫码
    var flag: Boolean = false

    // Speedata扫码器
    private lateinit var scanCode: ScanDecode

    override fun open() {
        try {
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
                    } catch (ex: Exception) {
                        logError(ex.toString())
                    }
                }

                override fun getBarcodeByte(p0: ByteArray?) {
                }

            })
            logInfo("$logTag：扫码监听器已添加...")
        }catch (ex:Exception){
            logError("$logTag：扫码器开启失败：$ex")
        }
    }

    override fun detach() {
        this.flag = false
    }

    override fun reConnect() {
        this.flag = true
    }

    override fun close() {
        this.scanCode.stopScan()
        log("info","$logTag：扫码事件已停止")
    }

}