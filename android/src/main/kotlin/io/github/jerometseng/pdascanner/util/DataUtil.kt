package io.github.jerometseng.pdascanner.util

import android.content.Intent
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.core.BroadcastTag
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager.Companion.LOG_TAG
import java.nio.charset.Charset

/**
 * 获取数据的工具类
 * @author 曾兴顺
 */
class DataUtil {
    companion object {

        // 锁对象
        private val lock = Any()

        // 上一次发送条码的时间戳
        @Volatile
        private var lastEmitTimeStamp: Long? = 0

        // 上一次发送条码的内容
        @Volatile
        private var lastEmitBarcode: String? = ""


        /**
         * 从intent中获取数据并发送给flutter
         * @author 曾兴顺
         */
        fun sendDataFromIntent(
            intent: Intent?,
            broadCastTag: List<BroadcastTag>?,
            methodChannel: MethodChannel
        ) {
            val action = intent?.action ?: "未知广播"
            broadCastTag?.forEach {
                try {
                    var data = if (it.dataType == IntentDataType.BYTE_ARRAY) {
                        val dataByteArray = intent?.getByteArrayExtra(it.label)
                        if (dataByteArray != null && dataByteArray.isNotEmpty()) {
                            String(dataByteArray, Charset.forName("UTF8"))
                        } else ""
                    } else {
                        intent?.extras?.getString(it.label) ?: ""
                    }
                    data = data.trim()
                    if (data.isNotBlank() && isNotConflict(data)) {
                        Log.d(LOG_TAG, "触发广播：[$action]\t数据：$data")
                        methodChannel.invokeMethod(CodeEmitterManager.CODE_EMITTER_METHOD, data)
                    }
                } catch (ex: Throwable) {
                    Log.e(LOG_TAG, "从广播[${action}]获取数据时发生错误：$ex")
                }
            }
        }

        /**
         * 因为同时添加了多个action 不排除会有冲突的情况
         * 判断如果上次触发的时间与当前触发时间间隔小于 200 ms 则代表同一时刻触发了多次
         * 则丢弃
         */
        private fun isNotConflict(data: String?): Boolean {
            synchronized(lock){
                val now = System.currentTimeMillis()
                return if (now - (lastEmitTimeStamp ?: 0) > 200 || (lastEmitBarcode ?: "") != data) {
                    lastEmitBarcode = data
                    lastEmitTimeStamp = now
                    true
                } else {
                    false
                }
            }
        }
    }

    /**
     * intent中的数据类型
     * String类型和ByteArray类型
     */
    enum class IntentDataType {
        STRING, BYTE_ARRAY
    }
}
