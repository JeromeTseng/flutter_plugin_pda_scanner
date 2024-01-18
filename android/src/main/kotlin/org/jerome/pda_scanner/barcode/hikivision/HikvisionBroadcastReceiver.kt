package org.jerome.pda_scanner.barcode.hikivision

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager

class HikvisionBroadcastReceiver(
    private val methodChannel: MethodChannel
) : BroadcastReceiver(){

    companion object{
        /**
         * android.intent.ACTION_DECODE_DATA
         */
        const val action_01 = "android.intent.ACTION_DECODE_DATA"
        const val action_01_label = "barcode"

        /**
         * com.service.scanner.data
         */
        const val action_02 = "com.service.scanner.data"
        const val action_02_label = "ScanCode"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            var barcode : String? = null
            val action : String? = intent.action
            if(action_01.equals(action,true)){
                barcode = intent.extras?.getString(action_01_label)
            }else if(action_02.equals(action_02_label)){
                barcode = intent.getStringExtra(action_02_label)
            }
            if(barcode != null){
                methodChannel.invokeMethod(CodeEmitterManager.CODE_EMITTER_METHOD, barcode)
            }
        }
    }
}