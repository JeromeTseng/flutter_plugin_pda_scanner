package io.github.jerometseng.pdascanner.core

import io.github.jerometseng.pdascanner.util.DataUtil

/**
 * 广播数据标签实体
 * @author 曾兴顺
 */
class BroadcastTag(
    val label :String,
    val dataType: DataUtil.IntentDataType = DataUtil.IntentDataType.STRING
)