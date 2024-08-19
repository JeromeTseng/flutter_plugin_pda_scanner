package io.github.jerometseng.pdascanner.pda_type.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager

/**
 * 自定义扫描器配置（广播）
 * @author 曾兴顺
 */
class CustomConfig(
    // 广播行为
    private val action: String,
    // 数据标签
    private val label:String,
    // 上下文
    private val context: Context,
    // 与flutter通信的通道
    private val methodChannel: MethodChannel
) : CodeEmitterManager(context,methodChannel)  {

    private val TAG = "CUSTOM"

    private var broadcastReceiver: BroadcastReceiver? = null

    override fun open() {
        try {
            if (this.broadcastReceiver == null) {
                val intentFilter = IntentFilter()
                intentFilter.addAction(action)
                this.broadcastReceiver = CustomBroadCastReceiver(action,label,methodChannel)
                context.registerReceiver(
                    this.broadcastReceiver, intentFilter
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