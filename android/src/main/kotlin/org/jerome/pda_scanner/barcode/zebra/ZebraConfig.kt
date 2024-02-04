package org.jerome.pda_scanner.barcode.zebra

import android.content.Context
import android.os.Build
import android.util.Log
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.BarcodeManager.ConnectionState
import com.symbol.emdk.barcode.BarcodeManager.ScannerConnectionListener
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.ScanDataCollection.ScanData
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.ScannerInfo
import com.symbol.emdk.barcode.ScannerResults
import com.symbol.emdk.barcode.StatusData
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jerome.pda_scanner.barcode.CodeEmitterManager
import java.util.Date

class ZebraConfig (
    private var context: Context,
    private var methodChannel: MethodChannel
): CodeEmitterManager(),
    EMDKManager.EMDKListener,
    Scanner.DataListener,
    Scanner.StatusListener,
    ScannerConnectionListener {

    private val TAG = "ZEBRA"

    // emdk 管理器
    private var emdkManager: EMDKManager? = null

    // 条码管理器
    private var barcodeManager: BarcodeManager? = null

    // 扫码器
    private var scanner: Scanner? = null

    // -- 继承自 CodeEmitterManager --
    override fun open() {
        try {
            val emdkResults = EMDKManager.getEMDKManager(context, this)
            if (emdkResults.statusCode == EMDKResults.STATUS_CODE.SUCCESS) {
                logInfo("${TAG}：EMDK实例获取成功...")
            }else{
                logError("${TAG}：获取EMDK实例失败!！")
            }
        } catch (ex: Exception) {
            logError("${TAG}：获取EMDK实例失败: $ex")
        }
    }

    // =======================

    // -- 打开EMDK服务 --
    override fun onOpened(emdk: EMDKManager?) {
        try {
            this.emdkManager = emdk
            this.barcodeManager =
                emdkManager?.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
            if(this.barcodeManager != null){
                this.barcodeManager?.addConnectionListener(this)
                logInfo("ZEBRA：barcodeManager连接器添加（EMDK服务打开成功）")
                this.startScan()
            }
        } catch (ex: Exception) {
            logError("${TAG}：EMDKListener.onOpened: $ex")
        }

    }

    override fun detach() {
    }

    override fun reConnect() {
    }

    override fun close() {
        this.deInitScanner()
        this.barcodeManager?.removeConnectionListener(this)
        this.barcodeManager = null
        this.emdkManager?.release(EMDKManager.FEATURE_TYPE.BARCODE)
        log("info","$TAG：扫码事件已停止...")
    }


    override fun onClosed() {
        try {
            this.barcodeManager?.removeConnectionListener(this)
            this.barcodeManager = null
            this.emdkManager?.release()
            this.emdkManager = null
            logInfo("${TAG}：资源已释放")
        } catch (ex: Exception) {
            logError("${TAG}：onClosed时发生错误！${ex.message}")
        }
    }
    // =======================

    // -- 继承自 DataListener --
    @OptIn(DelicateCoroutinesApi::class)
    override fun onData(scanDataCollection: ScanDataCollection?) {
        try {
            GlobalScope.launch {
                withContext(Dispatchers.Main){
                    if (scanDataCollection != null && scanDataCollection.result == ScannerResults.SUCCESS) {
                        val scanData: MutableList<ScanData> = scanDataCollection.scanData
                        scanData.forEach {
                            methodChannel.invokeMethod(CODE_EMITTER_METHOD, it.data)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            logError(ex.toString())
        }
    }

    // -- 继承自 StatusListener --
    override fun onStatus(statusData: StatusData?) {
        try {
            if (statusData != null) {
                val state = statusData.state
                when (state) {
                    StatusData.ScannerStates.IDLE -> {
                        Thread.sleep(100)
                        this.scanner!!.read()
                    }
                    else -> {}
                }
            }
        } catch (ex: Exception) {
            logError(ex.toString())
        }
    }

    // -- 继承自 ScannerConnectionListener --
    override fun onConnectionChange(scannerInfo: ScannerInfo?, connectionState: ConnectionState?) {
        try {
            when(connectionState){
                ConnectionState.CONNECTED -> initScanner()
                ConnectionState.DISCONNECTED -> deInitScanner()
                else -> {}
            }
        } catch (ex: Exception) {
            logError("ScannerConnectionListener.onConnectionChange: $ex")
        }
    }

    // 注销扫描
    private fun deInitScanner(){
        try {
            scanner?.cancelRead()
            scanner?.disable()
        }catch (e:Exception){
            logError("ZEBRA：deInitScanner 01:${e.message}")
        }
        try{
            scanner?.removeDataListener(this)
            scanner?.removeStatusListener(this)
        }catch (e:Exception){
            logError("ZEBRA：deInitScanner 02:${e.message}")
        }
        try{
            scanner?.release()
        }catch (e:Exception){
            logError("ZEBRA：deInitScanner 03:${e.message}")
        }
        scanner = null
    }

    // 启动扫描
    private fun startScan() {
        try{
            if (this.scanner == null) {
                initScanner()
            }
        }catch (e:Exception){
            logError("startScan：${e}")
        }
    }

    // 初始化扫描
    private fun initScanner(){
        try {
            if(scanner == null){
                scanner = barcodeManager!!.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
                if(scanner == null){
                    logInfo("扫描仪获取失败！！！")
                }else{
                    logInfo("扫描仪获取成功！！！")
                }
                if(scanner != null){
                    scanner!!.addDataListener(this)
                    scanner!!.addStatusListener(this)
                    try{
                        scanner!!.enable()
                        logInfo("ZEBRA：数据监听器和状态监听器添加成功！")
                    }catch (e:ScannerException){
                        logError("ZEBRA：initScanner ${e.message}")
                    }
                }
            }
        }catch (e:Exception){
            logError("initScanner：$e")
        }
    }

    private fun logInfo(infoMessage: String) {
        log("info", infoMessage)
    }

    private fun logError(infoMessage: String) {
        log("error", infoMessage)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun log(logType: String, infoMessage: String) {
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                sendLogMessage(methodChannel,"${logType}###&&&***${Date().time}###&&&***$infoMessage")
                if(logType=="info"){
                    Log.i(LOG_TAG,infoMessage)
                }else{
                    Log.e(LOG_TAG,infoMessage)
                }
            }
        }
    }

}