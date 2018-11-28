package com.raqun.worldcup.ui.picture.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import java.io.IOException
import android.graphics.Rect
import android.util.Log
import android.view.*


/**
 * Created by tyln on 4.06.2018.
 */
@SuppressLint("ViewConstructor")
class CameraPreview(context: Context?,
                    private var camera: Camera,
                    private val displayRotation: Int) : SurfaceView(context), SurfaceHolder.Callback {
    companion object {
        private const val TAG = "TAG"
        private const val FOCUS_AREA_SIZE = 300
    }

    val surfaceHolder: SurfaceHolder = holder
    private var previewSize: Camera.Size? = null
    private val supportedPreviewSizes: MutableList<Camera.Size>?

    init {
        surfaceHolder.addCallback(this)
        supportedPreviewSizes = camera.parameters.supportedPreviewSizes
    }

    private val surfaceViewTouchListener: View.OnTouchListener = OnTouchListener { v, event ->
        camera.cancelAutoFocus()
        val focusRect = calculateFocusArea(event.x, event.y)
        val parameters = camera.parameters
        if (parameters.focusMode == Camera.Parameters.FOCUS_MODE_AUTO) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }
        if (parameters.maxNumFocusAreas > 0) {
            val areaList = ArrayList<Camera.Area>()
            areaList.add(Camera.Area(focusRect, 1000))
            parameters.focusAreas = areaList
        }
        try {
            camera.cancelAutoFocus()
            camera.parameters = parameters
            camera.startPreview()
            camera.autoFocus { _, cam ->
                if (cam.parameters.focusMode == Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                    val parameters = cam.parameters;
                    parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    if (parameters.maxNumFocusAreas > 0) {
                        parameters.focusAreas = null
                    }
                    camera.parameters = parameters
                    camera.startPreview()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@OnTouchListener true
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        setOnTouchListener(surfaceViewTouchListener)
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera.setPreviewDisplay(holder)
            camera.setDisplayOrientation(displayRotation)
            camera.startPreview()
        } catch (e: IOException) {
            Log.d(TAG, "Error setting camera preview: " + e.message)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (holder?.surface == null) {
            // preview surface does not exist
            return
        }
        // stop preview before making changes
        try {
            camera.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            val parameters = camera.parameters
            val bestPictureSize = getBestPictureSize(width, height, parameters)
            bestPictureSize?.let {
                parameters.setPictureSize(it.width, it.height)
            }
            previewSize?.let {
                parameters.setPreviewSize(it.width, it.height)
            }

            camera.parameters = parameters
            camera.setPreviewDisplay(holder)
            camera.startPreview()
        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes,width, height)
        }
    }

    /*
    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = h.toDouble() / w
        if (sizes == null) return null
        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }
        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }

    private fun getBestPreviewSize(width: Int, height: Int): Camera.Size? {
        val sizes = camera.parameters.supportedPreviewSizes ?: return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        var minWidthDiff = 0
        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.width - width) < minDiff) {
                    if (size.width > width) {
                        if (minWidthDiff == 0) {
                            minWidthDiff = size.width - width
                            optimalSize = size
                        } else if (Math.abs(size.width - width) < minWidthDiff) {
                            minWidthDiff = size.width - width
                            optimalSize = size

                        }
                        minDiff = Math.abs(size.width - width).toDouble()
                    }
                }
            }
        }

        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - height) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - height).toDouble()
                }
            }
        }
        return optimalSize
    } */


    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {

        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        val ratio = h.toDouble() / w
        var minDiff = java.lang.Double.MAX_VALUE
        var newDiff: Double
        for (size in sizes) {
            newDiff = Math.abs(size.width.toDouble() / size.height - ratio)
            if (newDiff < minDiff) {
                optimalSize = size
                minDiff = newDiff
            }
        }
        return optimalSize
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        // no-op
    }

    private fun getBestPictureSize(width: Int, height: Int, parameters: Camera.Parameters): Camera.Size? {
        var bestSize: Camera.Size?
        val sizeList = parameters.supportedPictureSizes
        bestSize = sizeList[0]
        for (i in 1 until sizeList.size) {
            if (sizeList[i].width * sizeList[i].height > bestSize!!.width * bestSize.height) {
                bestSize = sizeList[i]
            }
        }
        return bestSize
    }

    private fun calculateFocusArea(x: Float, y: Float): Rect {
        val left = clamp(java.lang.Float.valueOf(x / width * 2000 - 1000).toInt(), FOCUS_AREA_SIZE)
        val top = clamp(java.lang.Float.valueOf(y / height * 2000 - 1000).toInt(), FOCUS_AREA_SIZE)
        return Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE)
    }

    private fun clamp(touchCoordinateInCameraReper: Int, focusAreaSize: Int): Int {
        return if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                1000 - focusAreaSize / 2
            } else {
                -1000 + focusAreaSize / 2
            }
        } else {
            touchCoordinateInCameraReper - focusAreaSize / 2
        }
    }

    fun turnFlashOnOrOff() {
        try {
            camera.stopPreview()
        } catch (e: Exception) {
            // ignore
        }

        val params = camera.parameters
        params?.let {
            if (params.flashMode == Camera.Parameters.FLASH_MODE_TORCH) {
                params.flashMode = Camera.Parameters.FLASH_MODE_OFF
                //flash.setImageResource(R.mipmap.baseline_flash_off_white_24dp)
            } else {
                params.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                //flash.setImageResource(R.mipmap.baseline_flash_on_white_24dp)
            }
            camera.setPreviewDisplay(holder)
            try {
                camera.parameters = params
            } catch (e: Exception) {
                e.printStackTrace()
            }
            camera.startPreview()
        }
    }
}


