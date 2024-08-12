package io.github.jerometseng.pdascanner.pda_type.seuic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager

class Cruise5GBroadCastReceiver(
    private val methodChannel: MethodChannel
):BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        var barcode = bundle?.getString("scannerdata");
        if(barcode != null){
            barcode = barcode.replace("\\s*|\r|\n|\t".toRegex(),"")
            methodChannel.invokeMethod(CodeEmitterManager.CODE_EMITTER_METHOD, barcode)
        }
    }
}