package org.jerome.pda_scanner.barcode.invengo

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager
import org.jerome.pda_scanner.barcode.zebra.ZebraConfig
import java.util.Date

class InvengoConfig(
    // 上下文
    private var context: Context,
    // 与flutter通信的通道
    private var methodChannel: MethodChannel

) : CodeEmitterManager() {

    private val TAG = "INVENGO"

    private var broadcastReceiver: BroadcastReceiver? = null

    companion object{

        private val SUPPORTED_DEVICE: MutableList<String> = mutableListOf("K71V1_64_bsp")

        /**
         * 判断是否为 ZEBRA 类型的设备
         * @author 曾兴顺  2024/01/16
         */
        fun isThisDevice(): Boolean {
            // 设备型号名称
            val modelName = Build.MODEL.uppercase()
            return SUPPORTED_DEVICE.find { it.uppercase() == modelName } != null
        }
    }

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    override fun open() {
        try {
            if (this.broadcastReceiver == null) {
                val intentFilter = IntentFilter()
                intentFilter.addAction("android.rfid.FUN_KEY")
                intentFilter.addAction("android.intent.action.FUN_KEY")
                intentFilter.addAction("com.rfid.SCAN")
                this.broadcastReceiver = InvengoBroadcastReceiver(methodChannel)
                context.registerReceiver(
                    this.broadcastReceiver, intentFilter,
                    Context.RECEIVER_EXPORTED,
                )
                logInfo("${TAG}：扫码事件广播已监听...[android.rfid.FUN_KEY]，[android.intent.action.FUN_KEY]，[com.rfid.SCAN]")
            }
        } catch (ex: Exception) {
            logError("${TAG}：设备初始化失败：${ex}")
        }
    }

    override fun detach() {
        this.close()
    }

    override fun reConnect() {
        this.open()
    }

    override fun close() {
        if (this.broadcastReceiver != null) {
            context.unregisterReceiver(this.broadcastReceiver)
            this.broadcastReceiver = null
            logInfo("${TAG}：广播已移除...[android.rfid.FUN_KEY]，[android.intent.action.FUN_KEY]，[com.rfid.SCAN]")
        }
    }

    fun logInfo(infoMessage: String) {
        log("info", infoMessage)
    }

    fun logError(infoMessage: String) {
        log("error", infoMessage)
    }

    fun log(logType: String, infoMessage: String) {
        sendLogMessage(methodChannel,"${logType}###&&&***${Date().time}###&&&***$infoMessage")
        if(logType=="info"){
            Log.i(LOG_TAG,infoMessage)
        }else{
            Log.e(LOG_TAG,infoMessage)
        }
    }

}