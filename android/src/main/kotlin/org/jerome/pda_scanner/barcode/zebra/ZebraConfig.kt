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
import com.symbol.emdk.barcode.ScannerInfo
import com.symbol.emdk.barcode.ScannerResults
import com.symbol.emdk.barcode.StatusData
import io.flutter.plugin.common.MethodChannel
import org.jerome.pda_scanner.barcode.CodeEmitterManager

class ZebraConfig(
    // 上下文
    private var context: Context,
    // 与flutter通信的通道
    private var methodChannel: MethodChannel

) : CodeEmitterManager(),
    EMDKManager.EMDKListener,
    Scanner.DataListener,
    Scanner.StatusListener,
    ScannerConnectionListener  {

        // emdk 管理器
        private var emdkManager : EMDKManager? = null
        // 条码管理器
        private var barcodeManager : BarcodeManager? = null
        // 扫码器
        private var scanner : Scanner? = null
        // 设备集合
        private var deviceList : List<ScannerInfo>? = null
        // 扫码器索引
        private var scannerIndex : Int = 0
        // 是否继续
        private var continuousMode : Boolean = false
        // 是否要触发扫码 / 当前程序是否正在前台运行 后台运行的话设置false
        var flag : Boolean = false

    // 静态方法
    companion object {
        // 支持 Speedata（思必拓）扫码的设备列表
        private val SUPPORTED_DEVICE: MutableList<String> = mutableListOf(
            "CC5000-10","CC600/CC6000",
            "EC30","EC50/EC55","ET40","ET45","ET51","ET56","ET5X",
            "MC18","MC20","MC2200","MC27","MC32","MC33","MC33ax","MC3300x","MC3330 RFID",
            "MC40","MC67","MC92","MC9300",
            "TC15","TC20","TC21","TC22","TC25","TC26","TC27","TC51","TC52","TC52ax","TC52x",
            "TC53","TC55","TC56","TC57","TC57x","TC58","TC70","TC70x","TC72","TC73","TC75",
            "TC75x","TC77","TC78","TC8000","TC8300",
            "VC8300","VC80x","WS50","WT6000","WT6300",
            "L10A","PS20"
        )

        /**
         * 判断是否为 Speedata 类型的设备
         * @author 曾兴顺  2024/01/16
         */
        fun isThisDevice(): Boolean {
            // 设备型号名称
            val modelName = Build.MODEL.uppercase()
            return SUPPORTED_DEVICE.find { it == modelName } != null
        }

    }

    // -- 继承自 CodeEmitterManager --
    override fun open() {
        try{
            this.flag = true
            val emdkResults = EMDKManager.getEMDKManager(context.applicationContext, this)
            if(emdkResults.statusCode != EMDKResults.STATUS_CODE.SUCCESS){
                super.emitErrorMessage(methodChannel,"来自ZEBRA设备，获取EMDK实例失败！")
            }
        }catch (ex:Exception){
            super.emitErrorMessage(methodChannel,"来自ZEBRA设备，获取EMDK实例失败: $ex")
        }
    }

    override fun detach() {
        this.flag = false
    }

    override fun reConnect() {
        this.flag = true
    }

    override fun close() {
        this.disposeScanner()
        this.barcodeManager?.removeConnectionListener(this)
        this.barcodeManager = null
        this.emdkManager?.release(EMDKManager.FEATURE_TYPE.BARCODE)
    }

    // =======================

    // -- 继承自 EMDKListener --
    override fun onOpened(emdkManager: EMDKManager?) {
        try {
            this.emdkManager = emdkManager
            this.barcodeManager = emdkManager?.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
            this.barcodeManager?.addConnectionListener(this)
            this.deviceList = this.barcodeManager?.supportedDevicesInfo
            if(this.scanner != null && this.scanner!!.isEnabled){
                this.scanner!!.read()
                this.continuousMode = true
            }
        }catch (ex : Exception){
            Log.e("ZEBRA", "EMDKListener.onOpened: $ex")
            super.emitErrorMessage(methodChannel,"来自ZEBRA设备，EMDKListener.onOpened: $ex")
        }

    }

    override fun onClosed() {
        try {
            this.barcodeManager?.removeConnectionListener(this)
            this.barcodeManager = null
            this.emdkManager?.release()
        }catch (ex:Exception){
            super.emitErrorMessage(methodChannel,"来自ZEBRA设备，onClosed时发生错误！${ex.message}")
        }
    }
    // =======================

    // -- 继承自 DataListener --
    override fun onData(scanDataCollection: ScanDataCollection?) {
        try{
            if (scanDataCollection != null && scanDataCollection.result == ScannerResults.SUCCESS){
                val scanData : MutableList<ScanData> = scanDataCollection.scanData
                scanData.forEach {
                    if(this.flag){
                        methodChannel.invokeMethod(CODE_EMITTER_METHOD, it.data)
                    }
                }
            }
        }catch (ex:Exception){
            super.emitErrorMessage(methodChannel,"来自ZEBRA设备，触发扫码时发生错误！${ex.message}")
        }
    }

    // -- 继承自 StatusListener --
    override fun onStatus(statusData: StatusData?) {
        try {
            if(statusData != null){
                val state = statusData.state
                when(state){
                    StatusData.ScannerStates.IDLE -> {
                        if(this.continuousMode){
                            Thread.sleep(100)
                            this.scanner?.read()
                        }
                    }
                    else -> {}
                }
            }
        }catch (ex:Exception){
            super.emitErrorMessage(methodChannel,"来自ZEBRA设备，onStatus: $ex")
        }
    }
    // -- 继承自 ScannerConnectionListener --
    override fun onConnectionChange(scannerInfo: ScannerInfo?, connectionState: BarcodeManager.ConnectionState?) {
        try{
            if(scannerInfo == null){
                return
            }
            if (this.deviceList != null && this.deviceList!!.isNotEmpty()){
                this.deviceList!!
                    .map { it.friendlyName }
                    .contains(scannerInfo.friendlyName)
                    .apply {
                        if(this && connectionState != null){
                            when(connectionState){
                                ConnectionState.CONNECTED -> this@ZebraConfig.initScanner()
                                else -> this@ZebraConfig.disposeScanner()
                            }
                        }
                    }

            }
        }catch (ex:Exception){
            Log.e("ZEBRA", "ScannerConnectionListener.onConnectionChange: $ex" )
            super.emitErrorMessage(methodChannel,"来自ZEBRA设备，ScannerConnectionListener.onConnectionChange: $ex")
        }
    }

    // 初始化扫码器
    private fun initScanner() : Unit{
        if(this.scanner == null){
            if(this.deviceList == null || this.deviceList!!.isEmpty()){
                super.emitErrorMessage(methodChannel,"来自ZEBRA设备，无法获得指定的扫描仪设备，请关闭并重新启动应用程序！")
                return
            }
            this.scanner = this.barcodeManager?.getDevice(this.deviceList!!.get(this.scannerIndex))
            this.scanner?.addDataListener(this)
            this.scanner?.addStatusListener(this)
            try {
                this.scanner?.enable()
            }catch (ex: Exception){
                Log.e("ZEBRA", "initScanner: ${ex.message}")
                super.emitErrorMessage(methodChannel,"来自ZEBRA设备，initScanner时出错！${ex.message}")
            }
        }
    }

    // 释放扫码器
    private fun disposeScanner(){
        try{
            if(this.scanner != null){
                this.scanner?.cancelRead();
                this.scanner?.disable();
                this.scanner?.removeDataListener(this)
                this.scanner?.removeStatusListener(this)
                this.scanner?.release()
                this.scanner = null
            }
        }catch (ex:Exception){
            super.emitErrorMessage(methodChannel,"来自ZEBRA设备，释放扫码器出错！${ex.message}")
        }
    }

}