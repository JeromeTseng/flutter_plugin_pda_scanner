package io.github.jerometseng.pdascanner.pda_type.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.core.ActionContainer.Companion.broadCastActionAndDataLabelMap
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager

/**
 * 通用广播配置
 * @author: 曾兴顺
 */
class CommonConfig(
    // 上下文
    private val context: Context,
    // 与flutter通信的通道
    private val methodChannel: MethodChannel
) : CodeEmitterManager(methodChannel) {

    private val logTag = "COMMON"

    private var broadcastReceiver: BroadcastReceiver? = null

    override fun open() {
        try {
            if (this.broadcastReceiver == null) {
                val intentFilter = IntentFilter()
                broadCastActionAndDataLabelMap.keys.forEach{
                    intentFilter.addAction(it)
                }
                this.broadcastReceiver = CommonBroadcastReceiver(methodChannel)
                context.registerReceiver(
                    this.broadcastReceiver, intentFilter
                )
                logInfo("${logTag}：已加载通用广播...")
            }
        } catch (ex: Exception) {
            logError("${logTag}：通用广播初始化失败：${ex}")
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
            logInfo("${logTag}：通用广播已移除...")
        }
    }

}