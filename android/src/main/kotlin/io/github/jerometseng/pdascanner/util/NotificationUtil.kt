package io.github.jerometseng.pdascanner.util

import android.annotation.TargetApi
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.util.Log

@TargetApi(Build.VERSION_CODES.S)
class NotificationUtil {
    private val tag = "NotificationUtil"
    private val toneGenerator:ToneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION,100)

    // 监视器对象锁
    private val toneGeneratorLock = Any()

    fun errorSound(){
        try {
            synchronized(toneGeneratorLock){
                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 1000)
            }
        }catch (e:Exception){
            Log.e(tag,"$e")
        }
    }

}