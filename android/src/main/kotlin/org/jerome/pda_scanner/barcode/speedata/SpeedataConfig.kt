package org.jerome.pda_scanner.barcode.speedata

import android.content.Context
import android.os.Build
import android.util.Log
import com.scandecode.ScanDecode
import com.scandecode.inf.ScanInterface.OnScanListener
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager
import java.util.Date

class SpeedataConfig(
    // 上下文
    private var context: Context,
    // 与flutter通信的通道
    private var methodChannel: MethodChannel

) : CodeEmitterManager() {

    private val TAG = "SPEEDATA"

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
            logInfo("$TAG：扫码监听器已添加...")
        }catch (ex:Exception){
            logError("$TAG：扫码器开启失败：$ex")
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
        log("info","$TAG：扫码事件已停止")
    }

    private fun logInfo(infoMessage: String) {
        log("info", infoMessage)
    }

    fun logError(infoMessage: String) {
        log("error", infoMessage)
    }

    private fun log(logType: String, infoMessage: String) {
        sendLogMessage(methodChannel,"${logType}###&&&***${Date().time}###&&&***$infoMessage")
        if(logType=="info"){
            Log.i(LOG_TAG,infoMessage)
        }else{
            Log.e(LOG_TAG,infoMessage)
        }
    }


}