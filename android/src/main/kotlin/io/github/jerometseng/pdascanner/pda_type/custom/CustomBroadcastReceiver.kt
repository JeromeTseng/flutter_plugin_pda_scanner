package io.github.jerometseng.pdascanner.pda_type.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.core.BroadcastTag
import io.github.jerometseng.pdascanner.util.DataUtil

/**
 * 自定义广播接收器
 * @author 曾兴顺
 */
class CustomBroadcastReceiver(
    private val tags: List<BroadcastTag>,
    private val methodChannel: MethodChannel,
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // 获取数据
        DataUtil.sendDataFromIntent(intent, tags,methodChannel)
    }

}