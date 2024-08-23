package io.github.jerometseng.pdascanner.pda_type.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.core.BroadCastTag
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager
import io.github.jerometseng.pdascanner.util.DataUtil

/**
 * 自定义扫描器配置（广播）
 * @author 曾兴顺
 */
class CustomConfig(
    // 广播行为
    private val action: String,
    // 数据标签
    private val label: String,
    // 上下文
    private val context: Context,
    // 与flutter通信的通道
    private val methodChannel: MethodChannel,
    // 数据类型
    private val dataType: DataUtil.IntentDataType = DataUtil.IntentDataType.STRING
) : CodeEmitterManager(methodChannel) {

    private val logTag = "CUSTOM"

    private var broadcastReceiver: BroadcastReceiver? = null

    override fun open() {
        try {
            this.close()
            val intentFilter = IntentFilter()
            intentFilter.addAction(action)
            this.broadcastReceiver =
                CustomBroadCastReceiver(listOf(BroadCastTag(label,dataType)), methodChannel)
            context.registerReceiver(
                this.broadcastReceiver, intentFilter
            )
            logInfo("${logTag}：扫码广播已监听：[$action]\t数据标签：[$label]\t数据类型：${dataType.name}")
        } catch (ex: Exception) {
            logError("${logTag}：设备初始化失败：${ex}")
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
            logInfo("${logTag}：广播已移除...[$action]")
        }
    }
}