package com.raqun.worldcup.util

import android.content.pm.PackageManager

/**
 * Created by tyln on 1.06.2018.
 */
class PermissionUtil {
    companion object {
        fun checkIfPermissionsGranted(grantResult: IntArray) =
                grantResult.none { it != PackageManager.PERMISSION_GRANTED }
    }
}