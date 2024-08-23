package io.github.jerometseng.pdascanner.core

import io.github.jerometseng.pdascanner.util.DataUtil

/**
 * 广播容器
 * 所有的广播都要放在这里
 * @author 曾兴顺
 */
class ActionContainer {
    companion object {
        /**
         * 广播行为和数据标签容器
         * 如果已经集成的型号匹配不上 则加载该扫码配置，加入一下所有广播
         */
        val broadCastActionAndDataLabelMap = mapOf(
            // ChainWay 广播
            "com.scanner.broadcast" to listOf(BroadCastTag("data")),
            // 海康广播
            "com.service.scanner.data" to listOf(BroadCastTag("ScanCode")),
            "android.intent.action.SCANNER_SERVICE" to listOf(BroadCastTag("data")),
            "android.intent.ACTION_SCAN_OUTPUT" to listOf(BroadCastTag("data")),
            // 远望谷广播
            "com.rfid.SCAN" to listOf(
                BroadCastTag(
                    "data",
                    dataType = DataUtil.IntentDataType.BYTE_ARRAY
                )
            ),
            // 东集SEUIC广播
            "com.android.server.scannerservice.broadcast" to listOf(BroadCastTag("scannerdata")),
            "barcode_broadcast" to listOf(BroadCastTag("scannerdata")),
            // 优博讯广播
            "android.intent.ACTION_DECODE_DATA" to listOf(
                BroadCastTag("barcode_string"),
                BroadCastTag("barcode")
            ),
            "android.senraise.scan" to listOf(BroadCastTag("result")),
            // SUNMI商米广播分
            "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED" to listOf(BroadCastTag("data")),
            // NewBland
            "nlscan.action.SCANNER_RESULT" to listOf(BroadCastTag("SCAN_BARCODE1")),
            // IDATA 盈达
            "android.intent.action.SCANRESULT" to listOf(BroadCastTag("value")),
            // MOBYDATA
            "com.android.decodewedge.decode_action" to listOf(BroadCastTag("com.android.decode.intentwedge.barcode_string")),
            // JOYREE 巨历
            "android.intent.action.BARCODEDATA" to listOf(BroadCastTag("barcode_result")),
            // "N60"
            "scan.rcv.message" to listOf(
                BroadCastTag("barcodeData"),
                BroadCastTag("barocode", dataType = DataUtil.IntentDataType.BYTE_ARRAY)
            ),
            // "ALPS"
            "com.barcode.sendBroadcast" to listOf(BroadCastTag("BARCODE")),
            // SHINIOW
            "com.android.server.scannerservice.shinow" to listOf(BroadCastTag("scannerdata")),
            // 西域ehsy
            "com.ehsy.warehouse.action.BARCODE_DATA" to listOf(BroadCastTag("data")),
            // 霍尼韦尔
            "com.honeywell.decode.intent.action.EDIT_DATA" to listOf(BroadCastTag("data")),
        )
    }
}