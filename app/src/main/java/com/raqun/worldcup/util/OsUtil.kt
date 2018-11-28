package com.raqun.worldcup.util

import android.os.Build

/**
 * Created by tyln on 1.06.2018.
 */
class OsUtil {
    companion object {
        fun hasMarshmellow() = Build.VERSION.SDK_INT >= 23

        fun isKitKat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }
}