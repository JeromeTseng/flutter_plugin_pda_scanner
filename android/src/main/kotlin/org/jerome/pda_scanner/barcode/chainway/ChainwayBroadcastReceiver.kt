package org.jerome.pda_scanner.barcode.chainway

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager

class ChainwayBroadcastReceiver(
    private val methodChannel: MethodChannel
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            val barcode = intent.getStringExtra("data")
            val status = intent.getStringExtra("SCAN_STATE")
            if(status == null || !status.equals("cancel",true)){
                methodChannel.invokeMethod(CodeEmitterManager.CODE_EMITTER_METHOD, barcode ?: "scan fail")
            }
        }
    }
}