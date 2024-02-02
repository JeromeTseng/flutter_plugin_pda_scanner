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

    // 设备集合
    private var deviceList: List<ScannerInfo>? = null

    // 扫码器索引
    private var scannerIndex: Int = 0

    // 是否继续
    private var continuousMode: Boolean = true

    // 静态方法
    companion object {
        // 支持 Speedata（思必拓）扫码的设备列表
        private val SUPPORTED_DEVICE: MutableList<String> = mutableListOf(
            "CC5000-10","CC600/CC6000","EC30","EC50/EC55",
            "ET40","ET45","ET51","ET56","ET5X","MC18","MC20",
            "MC2200","MC27","MC32","MC33","MC33ax","MC3300x","MC3330 RFID",
            "MC40", "MC67","MC92","MC9300","TC15","TC20","TC21","TC22","TC25",
            "TC26","TC27","TC51","TC52","TC52ax","TC52x","TC53","TC55",
            "TC56","TC57","TC57x","TC58","TC70","TC70x",
            "TC72","TC73","TC75","TC75x",
            "TC77","TC78","TC8000","TC8300","VC8300",
            "VC80x","WS50","WT6000","WT6300","L10A","PS20"
        )

        /**
         * 判断是否为 ZEBRA 类型的设备
         * @author 曾兴顺  2024/01/16
         */
        fun isThisDevice(): Boolean {
            // 设备型号名称
            val modelName = Build.MODEL.uppercase()
            return SUPPORTED_DEVICE.find { it.uppercase() == modelName } != null
        }

    }

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
                this.deviceList = this.barcodeManager?.supportedDevicesInfo
                logInfo("ZEBRA：barcodeManager连接器添加（EMDK服务打开成功）")
            }
            this.startScan()
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
    override fun onData(scanDataCollection: ScanDataCollection?) {
        try {
            logInfo("扫码事件触发")
            if (scanDataCollection != null && scanDataCollection.result == ScannerResults.SUCCESS) {
                val scanData: MutableList<ScanData> = scanDataCollection.scanData
                scanData.forEach {
                    methodChannel.invokeMethod(CODE_EMITTER_METHOD, it.data)
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
                        logInfo(statusData.friendlyName +"空闲中...")
                        if (this.continuousMode) {
                            Thread.sleep(100)
                            this.scanner!!.read()
                        }
                    }
                    StatusData.ScannerStates.SCANNING ->{
                        logInfo("扫码中....")
                    }
                    StatusData.ScannerStates.WAITING ->{
                        logInfo("等待中...")
                    }
                    StatusData.ScannerStates.ERROR -> {
                        logInfo("错误...")
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
            logInfo("链接改变！")
            var scannerName = ""
            val scannerNameExtScanner = scannerInfo?.friendlyName
            if(deviceList != null && deviceList!!.isNotEmpty()){
                scannerName = deviceList!!.get(scannerIndex).friendlyName
            }
            if(scannerName.equals(scannerNameExtScanner,true)){
                when(connectionState){
                    ConnectionState.CONNECTED -> initScanner()
                    ConnectionState.DISCONNECTED -> deInitScanner()
                    else -> {}
                }
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
            logInfo("ZEBRA：deInitScanner 01:取消扫码")
        }catch (e:Exception){
            logError("ZEBRA：deInitScanner 01:${e.message}")
        }
        try{
            scanner?.removeDataListener(this)
            scanner?.removeStatusListener(this)
            logInfo("ZEBRA：deInitScanner 01:移除监听")
        }catch (e:Exception){
            logError("ZEBRA：deInitScanner 02:${e.message}")
        }
        try{
            scanner?.release()
            logInfo("ZEBRA：deInitScanner 01:释放资源")
        }catch (e:Exception){
            logError("ZEBRA：deInitScanner 03:${e.message}")
        }
        scanner = null
    }

    // 启动扫描
    private fun startScan() {
        if (this.scanner == null) {
            initScanner()
        }
        try {
            if(scanner != null && scanner!!.isEnabled){
                scanner!!.read()
                continuousMode = true
                logInfo("准备读取条码...${scanner!!.scannerInfo.friendlyName}")
            }
        }catch (e:ScannerException){
            logError("ZEBRA：startScan:${e.message}")
        }
    }

    // 初始化扫描
    private fun initScanner(){
        if(scanner == null){
            if (deviceList != null && deviceList!!.isNotEmpty()){
                scanner = barcodeManager!!.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
//                scanner = barcodeManager!!.getDevice(deviceList!!.get(scannerIndex))
                if(scanner == null){
                    logInfo("扫描仪获取失败！！！")
                }else{
                    logInfo("扫描仪获取成功！！！")
                }
            }else{
                logError("ZEBRA：无法获取指定的扫描仪设备，请关闭并重新启动应用程序！")
                return
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
    }

    private fun logInfo(infoMessage: String) {
        log("info", infoMessage)
    }

    private fun logError(infoMessage: String) {
        log("error", infoMessage)
    }

    private fun log(logType: String, infoMessage: String) {
        sendLogMessage(methodChannel,"${logType}###&&&***${Date().time}###&&&***$infoMessage")
        if(logType=="info"){
            Log.i(LOG_TAG,infoMessage)
        }else{
            Log.e(LOG_TAG,infoMessage)
        }
    }

}