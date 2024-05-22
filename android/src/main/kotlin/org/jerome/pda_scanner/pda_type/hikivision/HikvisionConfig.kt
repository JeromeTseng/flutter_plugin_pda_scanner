package org.jerome.pda_scanner.pda_type.hikivision

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.pda_type.CodeEmitterManager

class HikvisionConfig(
    // 上下文
    private val context: Context,
    // 与flutter通信的通道
    private val methodChannel: MethodChannel

) : CodeEmitterManager(context,methodChannel) {
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
}