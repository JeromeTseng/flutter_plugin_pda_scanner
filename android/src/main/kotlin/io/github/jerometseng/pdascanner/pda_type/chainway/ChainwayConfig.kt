package io.github.jerometseng.pdascanner.pda_type.chainway

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.rscja.scanner.utility.ScannerUtility
import io.flutter.plugin.common.MethodChannel
import io.github.jerometseng.pdascanner.pda_type.CodeEmitterManager

class ChainwayConfig(
    // 上下文
    private val context: Context,
    // 与flutter通信的通道
    private val methodChannel: MethodChannel
) : CodeEmitterManager(context,methodChannel) {
    private val TAG = "CHAINWAY"


    private var scannerUtility: ScannerUtility? = null
    private var barcodeDataReceiver: BroadcastReceiver? = null

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    override fun open() {
        try {
            if (this.scannerUtility == null) {
                this.scannerUtility = ScannerUtility.getScannerInerface()
                this.scannerUtility?.setOutputMode(context, 2)
                this.scannerUtility?.setScanResultBroadcast(
                    context,
                    "com.scanner.broadcast",
                    "data"
                )
                this.scannerUtility?.setReleaseScan(context, false)
                this.scannerUtility?.setScanFailureBroadcast(context, true)
                this.scannerUtility?.setScanKey(context, 3, intArrayOf(139, 280))
                this.scannerUtility?.setContinuousScanRFID(context, false)
                this.scannerUtility?.setContinuousScanIntervalTimeRFID(context, 500)
                this.scannerUtility?.enablePlayFailureSound(context, false)
                this.scannerUtility?.enableEnter(context, false)
                this.scannerUtility?.setBarcodeEncodingFormat(context, 1)
                this.scannerUtility?.disableFunction(context, 11)
                this.scannerUtility?.enableFunction(context, 101)
                this.scannerUtility?.open(context)
                val intentFilter = IntentFilter("com.scanner.broadcast")

                this.barcodeDataReceiver = ChainwayBroadcastReceiver(methodChannel)

                context.registerReceiver(
                    this.barcodeDataReceiver,
                    intentFilter,
                    Context.RECEIVER_EXPORTED
                )
                logInfo("${TAG}：扫码事件广播已监听...[com.scanner.broadcast]")
            }
        } catch (ex: Exception) {
            logError("${TAG}：open时出错，${ex.message}")
        }
    }

    override fun detach() {
        this.close()
    }

    override fun reConnect() {
        this.open()
    }

    override fun close() {
        this.scannerUtility?.close(context)
        this.scannerUtility = null
        context.unregisterReceiver(barcodeDataReceiver)
        log("info","$TAG：广播已移除")
    }

}