package io.github.jerometseng.pdascanner.pda_type.hikivision

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.text.TextUtils

class HikvisionManager {

    companion object {
        const val SCAN_VOICE: String = "scanVoice"
        const val SCAN_SHOCK: String = "scanShock"
        const val SCAN_END_SYMBOL: String = "endSymbol"
        const val SCAN_SAVE_IMAGE_ENABLE: String = "saveImageEnable"
        const val SCAN_SAVE_IMAGE_MODE: String = "saveImageMode"
        const val SCAN_SAVE_IMAGE_PATH: String = "saveImagePath"
        const val SCAN_JPEG_IMAGE_QUALITY: String = "jpgImageQuality"
        const val SCAN_MODE: String = "scanMode"
        const val SCAN_INTERVAL_TIME: String = "intervalTime"
        const val SCAN_SINGLE_TIME: String = "singleTime"
        const val SCAN_CHARSET: String = "charset"
        const val SCAN_OCR_ENABLE: String = "ocrEnable"
        const val SCAN_OCR_ENABLE_CONF: String = "ocrEnableConf"
        const val SCAN_OCR_CONF: String = "ocrConf"
        const val SCAN_OUTPUT_MODE: String = "outputMode"
        const val SCAN_CODE_PREFIX: String = "scanCodePrefix"
        const val SCAN_CODE_SUFFIX: String = "scanCodeSuffix"
        const val SCAN_CODE_FILTER_PREFIX: String = "scanCodeFilterPrefix"
        const val SCAN_CODE_FILTER_SUFFIX: String = "scanCodeFilterSuffix"
        const val SCAN_CODE_FILTER_REPEATED: String = "scanCodeFilterRepeated"
        const val SCAN_CODE_TYPES: String = "scanCodeTypes"
        const val SCAN_RESULT_CODE: String = "ScanCode"
        const val SCAN_RESULT_PHONE: String = "ScanOcrPhone"
        const val SCAN_RESULT_PHONE_CONF: String = "ScanOcrPhoneConfidence"
        const val SCAN_RESULT_JPEG_DATA: String = "ScanJpegData"
        val TAG: String = HikvisionManager::class.java.simpleName
        const val PDA_SERVICE_LAUNCH_CLASS: String = "com.pda.service.broadcast.LaunchReceiver"
        const val PDA_SERVICE_CONTROLLED_CLASS: String =
            "com.pda.service.broadcast.ServiceControlReceiver"
        const val PDA_SERVICE_PACKAGENAME: String = "com.hikrobotics.pdaservice"
        const val ACTION_PDA_SERVICE_START: String = "com.service.scanner.start"
        const val ACTION_PDA_SERVICE_STOP: String = "com.service.scanner.stop"
        const val ACTION_PDA_SERVICE_SCAN_VOICE: String = "com.service.scanner.voice"
        const val ACTION_PDA_SERVICE_SCAN_SHOCK: String = "com.service.scanner.shock"
        const val ACTION_PDA_SERVICE_SCAN_OUTPUTMODE: String = "com.service.scanner.outputmode"
        const val ACTION_PDA_SERVICE_SCAN_ENDSYMBOL: String = "com.service.scanner.endsymbol"
        const val ACTION_PDA_SERVICE_SCAN_SAVEIMAGE: String = "com.service.scanner.saveimage"
        const val ACTION_PDA_SERVICE_SCAN_MODE: String = "com.service.scanner.scanmode"
        const val ACTION_PDA_SERVICE_SCAN_CHARSET: String = "com.service.scanner.charset"
        const val ACTION_PDA_SERVICE_SCAN_PREFIX: String = "com.service.scanner.prefix"
        const val ACTION_PDA_SERVICE_SCAN_SUFFIX: String = "com.service.scanner.suffix"
        const val ACTION_PDA_SERVICE_SCAN_FILTER_PREFIX: String =
            "com.service.scanner.filter.prefix"
        const val ACTION_PDA_SERVICE_SCAN_FILTER_SUFFIX: String =
            "com.service.scanner.filter.suffix"
        const val ACTION_PDA_SERVICE_SCAN_REPEATED_FILTER: String =
            "com.service.scanner.repeated.filter"
        const val ACTION_PDA_SERVICE_CODETYPE_ENABLE: String = "com.service.scanner.codetype"
        const val ACTION_PDA_SERVICE_OCR_ENABLE: String = "com.service.scanner.ocr.enable"
        const val ACTION_PDA_SERVICE_OCR_CONF_ENABLE: String = "com.service.scanner.ocrconf.enable"
        const val ACTION_PDA_SERVICE_OCR_CONF: String = "com.service.scanner.ocrconf"

        fun sendScanMode(
            context: Context?,
            intervalTime: Int = 3000,
            singleTime: Int = 3000
        ) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.scanmode")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("scanMode", "SINGLEMODE")
                mIntent.putExtra("intervalTime", intervalTime)
                mIntent.putExtra("singleTime", singleTime)
                context.sendBroadcast(mIntent)
            }
        }

        fun sendServiceEnable(context: Context?, isStartService: Boolean) {
            if (context != null) {
                val mIntent: Intent
                if (isStartService) {
                    mIntent = Intent("com.service.scanner.start")
                    mIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    mIntent.setComponent(
                        ComponentName(
                            PDA_SERVICE_PACKAGENAME,
                            "com.pda.service.broadcast.LaunchReceiver"
                        )
                    )
                    context.sendBroadcast(mIntent)
                } else {
                    mIntent = Intent("com.service.scanner.stop")
                    mIntent.setComponent(
                        ComponentName(
                            PDA_SERVICE_PACKAGENAME,
                            "com.pda.service.broadcast.LaunchReceiver"
                        )
                    )
                    context.sendBroadcast(mIntent)
                }
            }
        }

        fun sendScanVoiceEnable(context: Context?, voiceEnable: Boolean) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.voice")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra(SCAN_VOICE, voiceEnable)
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanShockEnable(context: Context?, voiceEnable: Boolean) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.shock")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("scanShock", voiceEnable)
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanOutputMode(context: Context?) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.outputmode")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("outputMode", "BROADCASTMODE")
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanEndSymbol(context: Context?) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.endsymbol")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                // 扫描结束的标志
                // NOTHING ENTER SPACE TAB
                mIntent.putExtra("endSymbol", "ENTER")
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanCharsetFormat(context: Context?) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.charset")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("charset", "UTF8")
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanAddPrefix(context: Context?, prefix: String?) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.prefix")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("scanCodePrefix", prefix?:"")
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanAddSuffix(context: Context?, suffix: String?) {
            if (context != null) {
                val mIntent = Intent(ACTION_PDA_SERVICE_SCAN_SUFFIX)
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("scanCodeSuffix", suffix?:"")
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanFilterPrefix(context: Context?, prefix: String?) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.filter.prefix")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("scanCodeFilterPrefix", prefix?:"")
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanFilterSuffix(context: Context?, suffix: String?) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.filter.suffix")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("scanCodeFilterSuffix", suffix?:"")
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanFilterRepeatedEnable(context: Context?, enable: Boolean) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.repeated.filter")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("scanCodeFilterRepeated", enable)
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanOcrEnable(context: Context?, enable: Boolean) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.ocr.enable")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("ocrEnable", enable)
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanOcrConfEnable(context: Context?, enableConf: Boolean) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.ocrconf.enable")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("ocrEnableConf", enableConf)
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanOcrConf(context: Context?, conf: Int) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.ocrconf")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("ocrConf", conf)
                context.sendBroadcast(mIntent)
            }
        }

        fun sendScanCodeTypes(context: Context?, codeTypes: Int) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.codetype")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("scanCodeTypes", codeTypes)
                context.sendBroadcast(mIntent)
            }
        }

        fun sendSaveImageMode(context: Context?, saveEnable: Boolean, mode: String, path: String?) {
            if (context != null) {
                val mIntent = Intent("com.service.scanner.saveimage")
                mIntent.setComponent(
                    ComponentName(
                        PDA_SERVICE_PACKAGENAME,
                        PDA_SERVICE_CONTROLLED_CLASS
                    )
                )
                mIntent.putExtra("saveImageMode", mode)
                mIntent.putExtra("saveImageEnable", saveEnable)
                mIntent.putExtra("saveImagePath", path)
                mIntent.putExtra("jpgImageQuality", 70)
                context.sendBroadcast(mIntent)
            }
        }

        fun isApkInstalled(context: Context, packageName: String?): Boolean {
            if (TextUtils.isEmpty(packageName)) {
                return false
            } else {
                try {
                    val info = context.packageManager.getApplicationInfo(packageName!!, 8192)
                    return true
                } catch (var3: NameNotFoundException) {
                    var3.printStackTrace()
                    return false
                }
            }
        }
    }
}