package org.jerome.pda_scanner.barcode.zebra

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager
import java.util.Date

class ZebraIntentConfig(
    // 上下文
    private var context: Context,
    // 与flutter通信的通道
    private var methodChannel: MethodChannel
) : CodeEmitterManager(){

    private val TAG = "ZEBRA"
    private val broadcastTag = "org.jerome.pda"

    private var broadcastReceiver: BroadcastReceiver? = null

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    override fun open() {
        try{
            if (this.broadcastReceiver == null) {
                val intentFilter = IntentFilter()
                intentFilter.addAction(broadcastTag)
                this.broadcastReceiver = ZebraBroadcastReceiver(methodChannel)
                context.registerReceiver(
                    this.broadcastReceiver, intentFilter,
                    Context.RECEIVER_EXPORTED,
                )
                logInfo("${TAG}：扫码事件广播已监听...")
            }
        }catch (e:Exception){
            logError("$TAG：扫码事件广播开启失败！")
        }
    }

    override fun detach() {
        this.close()
    }

    override fun reConnect() {
        this.open()
    }

    override fun close() {
        try{
            if (this.broadcastReceiver != null) {
                context.unregisterReceiver(this.broadcastReceiver)
                this.broadcastReceiver = null
                logInfo("${TAG}：广播已移除...${broadcastTag}")
            }
        }catch (e:Exception){
            logError("$TAG：广播已移除！${broadcastTag}")
        }
    }

    private fun logInfo(infoMessage: String) {
        log("info", infoMessage)
    }

    private fun logError(infoMessage: String) {
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