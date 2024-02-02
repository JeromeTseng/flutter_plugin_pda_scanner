package org.jerome.pda_scanner.barcode.zebra

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager

class ZebraBroadcastReceiver(
    private val methodChannel: MethodChannel
) : BroadcastReceiver() {

    companion object{
        const val decodeTag = "com.symbol.datawedge.data_string";
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val barcode = intent?.getStringExtra(decodeTag)?:""
        if(barcode.isNotBlank()){
            methodChannel.invokeMethod(CodeEmitterManager.CODE_EMITTER_METHOD, barcode)
        }
    }
}