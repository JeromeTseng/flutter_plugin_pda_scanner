package io.github.jerometseng.pdascanner.pda_type.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager
import java.nio.charset.Charset

/**
 * 自定义广播接收器
 * @author 曾兴顺
 */
class CustomBroadCastReceiver(
    // 广播行为
    private val action: String,
    // 数据标签
    private val label:String,
    private val methodChannel: MethodChannel
) : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(action.equals(intent?.action,ignoreCase = true)){
            var data = intent?.extras?.getString(label)
            if(data != null){
                data = data.replace("\\s*|\r|\n|\t".toRegex(),"")
                methodChannel.invokeMethod(CodeEmitterManager.CODE_EMITTER_METHOD, data)
            }else{
                val dataByteArray = intent?.getByteArrayExtra(label)
                if(dataByteArray != null && dataByteArray.isNotEmpty()){
                    try {
                        data = String(dataByteArray, Charset.forName("UTF8"))
                            .replace("\r\n", "")
                        methodChannel.invokeMethod(CodeEmitterManager.CODE_EMITTER_METHOD, data)
                    }catch (ex:Exception){
                        Log.e("CustomReceiver",ex.toString())
                    }
                }
            }
        }
    }

}