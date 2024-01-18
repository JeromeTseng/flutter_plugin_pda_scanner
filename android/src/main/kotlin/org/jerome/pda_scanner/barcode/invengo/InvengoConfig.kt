package org.jerome.pda_scanner.barcode.invengo

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager

class InvengoConfig (
    // 上下文
    private var context: Context,
    // 与flutter通信的通道
    private var methodChannel: MethodChannel

) : CodeEmitterManager() {

    private var broadcastReceiver : BroadcastReceiver? = null

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    override fun open() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.rfid.FUN_KEY")
        intentFilter.addAction("android.intent.action.FUN_KEY")
        intentFilter.addAction("com.rfid.SCAN")
        if(this.broadcastReceiver == null){
            this.broadcastReceiver = InvengoBroadcastReceiver(methodChannel)
            context.registerReceiver(
                this.broadcastReceiver, intentFilter,
                Context.RECEIVER_EXPORTED,
            )
        }
    }

    override fun detach() {
        this.close()
    }

    override fun reConnect() {
        this.open()
    }

    override fun close() {
        if(this.broadcastReceiver != null){
            context.unregisterReceiver(this.broadcastReceiver)
            this.broadcastReceiver = null
        }
    }

}