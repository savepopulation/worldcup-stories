package com.raqun.worldcup.util

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera


/**
 * Created by tyln on 4.06.2018.
 */
class CameraUtil {
    companion object {
        fun checkCameraHardware(context: Context): Boolean =
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

        fun getCameraInstance(cameraId: Int?): Camera? {
            var c: Camera? = null
            try {
                c = cameraId?.let {
                    Camera.open(cameraId)
                } ?: Camera.open()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return c // returns null if camera is unavailable
        }

        fun hasFlash(context: Context) = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }
}


