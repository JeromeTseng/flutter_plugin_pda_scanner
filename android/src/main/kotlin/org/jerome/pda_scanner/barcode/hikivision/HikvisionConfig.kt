package org.jerome.pda_scanner.barcode.hikivision

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager
import java.util.Date

class HikvisionConfig(
    // 上下文
    private var context: Context,
    // 与flutter通信的通道
    private var methodChannel: MethodChannel

) : CodeEmitterManager() {
    private val TAG = "HIKVISION"

    private var hikvisionBroadcastReceiver: BroadcastReceiver? = null

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    override fun open() {
        if (hikvisionBroadcastReceiver != null) return
        this.hikvisionBroadcastReceiver = HikvisionBroadcastReceiver(methodChannel)
        val intentFilter = IntentFilter()
        intentFilter.addAction(HikvisionBroadcastReceiver.action_01)
        intentFilter.addAction(HikvisionBroadcastReceiver.action_02)
        context.registerReceiver(
            this.hikvisionBroadcastReceiver, intentFilter,
            Context.RECEIVER_EXPORTED,
        )
        HikvisionManager.sendServiceEnable(context, true)
        HikvisionManager.sendScanMode(context)
        HikvisionManager.sendScanVoiceEnable(context, true)
        HikvisionManager.sendScanOutputMode(context)
        logInfo("${TAG}：扫码事件广播已监听...[android.intent.ACTION_DECODE_DATA]，[com.service.scanner.data]")
    }

    override fun detach() {
        this.close()
    }

    override fun reConnect() {
        this.open()
    }

    override fun close() {
        if (this.hikvisionBroadcastReceiver != null) {
            context.unregisterReceiver(this.hikvisionBroadcastReceiver)
            this.hikvisionBroadcastReceiver = null
        }
        HikvisionManager.sendServiceEnable(context, false)
        log("info","$TAG：广播已移除")
    }

    private fun logInfo(infoMessage: String) {
        log("info", infoMessage)
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