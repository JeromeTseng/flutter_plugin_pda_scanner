package io.github.jerometseng.pdascanner.pda_type.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.core.ActionContainer.Companion.broadCastActionAndDataLabelMap
import io.github.jerometseng.pdascanner.util.DataUtil

/**
 * 通用广播
 * @author 曾兴顺
 */
class CommonBroadcastReceiver(
    private val methodChannel: MethodChannel
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            // 发送数据
            DataUtil.sendDataFromIntent(
                intent,
                broadCastActionAndDataLabelMap[intent.action],
                methodChannel
            )
        }
    }
}