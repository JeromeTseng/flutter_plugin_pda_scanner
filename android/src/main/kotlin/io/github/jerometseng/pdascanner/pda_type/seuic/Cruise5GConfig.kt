package io.github.jerometseng.pdascanner.pda_type.seuic

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager

class Cruise5GConfig(
    // 上下文
    private val context: Context,
    // 与flutter通信的通道
    private val methodChannel: MethodChannel
) : CodeEmitterManager(context,methodChannel)  {

    private val TAG = "Seuic:Cruise"

    private var broadcastReceiver: BroadcastReceiver? = null

    private val action = "com.android.server.scannerservice.broadcast"

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    override fun open() {
        try {
            if (this.broadcastReceiver == null) {
                val intentFilter = IntentFilter()
                intentFilter.addAction(action)
                this.broadcastReceiver = Cruise5GBroadCastReceiver(methodChannel)
                context.registerReceiver(
                    this.broadcastReceiver, intentFilter,
                    Context.RECEIVER_EXPORTED,
                )
                logInfo("${TAG}：扫码事件广播已监听...[$action]")
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
            logInfo("${TAG}：广播已移除...[$action]")
        }
    }
}