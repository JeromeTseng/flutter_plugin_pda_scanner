package org.jerome.pda_scanner.barcode.invengo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager
import java.nio.charset.Charset

class InvengoBroadcastReceiver (
    private val methodChannel: MethodChannel
) : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            val dataByteArray = intent.getByteArrayExtra("data")
            if(dataByteArray != null && dataByteArray.isNotEmpty()){
                try {
                    val barcode = String(dataByteArray, Charset.forName("UTF8"))
                        .replace("\r\n", "")
                    methodChannel.invokeMethod(CodeEmitterManager.CODE_EMITTER_METHOD, barcode)
                }catch (ex:Exception){
                    Log.e("InvengoReceiver",ex.toString())
                }
            }
        }
    }

}