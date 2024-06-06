package org.jerome.pda_scanner.pda_type

import android.os.Build

class DeviceDetect {

    companion object {
        /**
         * 判断是否为 Speedata 类型的设备
         * @author 曾兴顺  2024/02/24
         */
        fun isSpeedataDevice(): Boolean {
            return mutableListOf(
                "SC40G", "FG40-4G/5G", "SC55G", "FG50-4G/5G",
                "HT35","SD60","FG60-4G/5G","ST55G","FG50RT-4G/5G",
                "ST55RT","FG60RT-4G/5G","KT50-UHF","SD50RT","T60",
                "T50","FG40","FG50RT","SC40G","SC55G"
            ).contains(Build.MODEL.uppercase())
        }

        /**
         * 判断是否为 Zebra 类型的设备
         * @author 曾兴顺  2024/02/24
         */
        fun isZebraDevice(): Boolean {
            return mutableListOf(
                "CC5000-10", "CC600/CC6000", "EC30", "EC50/EC55",
                "ET40", "ET45", "ET51", "ET56", "ET5X", "MC18", "MC20",
                "MC2200", "MC27", "MC32", "MC33", "MC33AX", "MC3300X", "MC3330 RFID",
                "MC40", "MC67", "MC92", "MC9300", "TC15", "TC20", "TC21", "TC22", "TC25",
                "TC26", "TC27", "TC51", "TC52", "TC52AX", "TC52X", "TC53", "TC55",
                "TC56", "TC57", "TC57X", "TC58", "TC70", "TC70X",
                "TC72", "TC73", "TC75", "TC75X", "TN28",
                "TC77", "TC78", "TC8000", "TC8300", "VC8300",
                "VC80X", "WS50", "WT6000", "WT6300", "L10A", "PS20"
            ).contains(Build.MODEL.uppercase())
        }

        /**
         * 判断是否为 Invengo 类型的设备
         * @author 曾兴顺  2024/02/24
         */
        fun isInvengoDevice(): Boolean {
            return mutableListOf(
                "K71V1_64_BSP"
            ).contains(Build.MODEL.uppercase())
        }

        /**
         * 判断是否为 海康类型的设备
         * @author 曾兴顺  2024/02/24
         */
        fun isHikvisionDevice(): Boolean {
            return mutableListOf(
                "DS-MDT201-5G-YC","DS-MDT201","DSMDT201"
            ).contains(Build.MODEL.uppercase())
        }

        /**
         * 判断设备是否支持扫码
         * @author 曾兴顺  2024/02/04
         */
        fun isThisDeviceSupported(): Boolean {
            return isSpeedataDevice() || isZebraDevice() || isInvengoDevice()
        }
    }
}