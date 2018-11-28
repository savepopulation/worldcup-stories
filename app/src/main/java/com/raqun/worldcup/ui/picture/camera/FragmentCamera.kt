package com.raqun.worldcup.ui.picture.camera

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.view.Surface
import android.view.View
import com.raqun.worldcup.R
import com.raqun.worldcup.ui.BaseFragment
import com.raqun.worldcup.ui.picture.PictureActivity
import com.raqun.worldcup.util.*
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tyln on 4.06.2018.
 */
class FragmentCamera : BaseFragment() {

    private var selectedImageUri: Uri? = null
    private var selectedImagePath: String? = null
    private var camera: Camera? = null
    private var rotation: Int = 0
    private var cameraPreview: CameraPreview? = null
    private var currentCamera: Int = Camera.CameraInfo.CAMERA_FACING_FRONT
    private val pictureCallback = Camera.PictureCallback { p0, _ ->

        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        photoFile?.let {
            try {
                val fos = FileOutputStream(photoFile)
                fos.write(p0)
                fos.close()
                handleImageFromCamera()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    override fun getLayoutRes(): Int = R.layout.fragment_camera

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_capture.setOnClickListener {
            try {
                camera?.takePicture(null, null, pictureCallback)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        button_gallery.setOnClickListener {
            checkPermissionsAndOpenGallery()
        }

        /*
        if (CameraUtil.hasFlash(context)) {
            flash.visibility = View.VISIBLE
            flash.setOnClickListener {
                if (OsUtil.hasMarshmellow()) {
                    val camManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                    camManager.setTorchMode(getCameraId().toString(), true)
                }
            }
        } else {
            flash.visibility = View.GONE
        } */

        switch_camera.setOnClickListener {
            switchCamera()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkPermissionsAndOpenCamera()
    }

    private fun switchCamera() {
        try {
            camera?.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //NB: if you don't release the current camera before switching, you app will crash
        camera?.release()

        //swap the id of the camera to be used
        currentCamera = if (currentCamera === android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        } else {
            Camera.CameraInfo.CAMERA_FACING_BACK
        }

        fl_camera.removeView(cameraPreview)
        openCamera()
    }

    private fun getDisplayRotation(): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(getCameraId(), info)
        val rotation = activity.windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result
    }

    private fun getCameraId(): Int {
        val numberOfCameras = Camera.getNumberOfCameras()
        var cameraInfo: Camera.CameraInfo
        for (i in 0 until numberOfCameras) {
            cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == currentCamera) {
                return i
            }
        }
        return 0
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (PermissionUtil.checkIfPermissionsGranted(grantResults)) {
            when (requestCode) {
                PictureActivity.REQUEST_PERMISSION_GRANT_FOR_CAMERA -> openCamera()
                PictureActivity.REQUEST_PERMISSION_GRANT_FOR_GALLERY -> openGallery()
            }
        } else {
            AlertUtil.alert(activity, "Please give all permissions to use app..")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureActivity.GALLERY_INTENT_CALLED -> handleImageFromGallery(requestCode, data)
                PictureActivity.GALLERY_KITKAT_INTENT_CALLED -> handleImageFromGallery(requestCode, data)
            }
        }
    }

    private fun checkPermissionsAndOpenCamera() {
        if (OsUtil.hasMarshmellow() &&
                (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(PictureActivity.permissions, PictureActivity.REQUEST_PERMISSION_GRANT_FOR_CAMERA)
        } else {
            openCamera()
        }
    }

    private fun checkPermissionsAndOpenGallery() {
        if (OsUtil.hasMarshmellow() &&
                (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(PictureActivity.permissions, PictureActivity.REQUEST_PERMISSION_GRANT_FOR_GALLERY)
        } else {
            openGallery()
        }
    }

    private fun handleImageFromCamera() {

        if (currentCamera == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            (activity as PictureActivity).setCurrentPicture(flipBitmap(getBitmapRotated(selectedImagePath!!, -90)))
        } else if (currentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
            (activity as PictureActivity).setCurrentPicture(getBitmapRotated(selectedImagePath!!, 90))
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun handleImageFromGallery(requestCode: Int, data: Intent?) {
        if (requestCode == PictureActivity.GALLERY_INTENT_CALLED) {
            selectedImageUri = data?.data
            selectedImagePath = GalleryUtil.getPath(selectedImageUri, activity)
        } else if (requestCode == PictureActivity.GALLERY_KITKAT_INTENT_CALLED) {
            selectedImageUri = data?.data
            // Check for the freshest data.
            if (selectedImageUri != null) {
                selectedImagePath = GalleryUtil.getPath(selectedImageUri, activity)
            }
        }

        if (!TextUtils.isEmpty(selectedImagePath)) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            BitmapFactory.decodeFile(selectedImagePath, options)
            val tempWidth = options.outWidth
            val tempHeight = options.outHeight

            val MAX_SIZE = resources.getDimensionPixelSize(
                    R.dimen.image_loader_post_width)
            var scale = 1

            if (tempHeight > MAX_SIZE || tempWidth > MAX_SIZE) {
                scale = if (tempWidth > tempHeight) {
                    Math.round(tempHeight.toFloat() / MAX_SIZE.toFloat())
                } else {
                    Math.round(tempWidth.toFloat() / MAX_SIZE)
                }
            }

            val op = BitmapFactory.Options()
            op.inSampleSize = scale

            try {
                val pic = BitmapFactory.decodeFile(selectedImagePath, op)
                (activity as PictureActivity).setCurrentPicture(pic)
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: Error) {
                e.printStackTrace()
            }
        }
    }

    private fun openCamera() {
        camera = CameraUtil.getCameraInstance(getCameraId())

        if (camera == null) {
            if (currentCamera == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                switchCamera()
            } else {
                AlertUtil.alert(activity, "There're no available cameras on your phone.")
            }
        } else {
            rotation = getDisplayRotation()
            cameraPreview = CameraPreview(activity, camera!!, rotation)
            fl_camera.addView(cameraPreview)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        selectedImagePath = image.absolutePath
        return image
    }

    private fun openGallery() {
        if (!OsUtil.isKitKat()) {
            val intent = Intent()
            intent.type = "image/jpeg"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, getString(R.string.upload_picker_title)), PictureActivity.GALLERY_INTENT_CALLED)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/jpeg"
            startActivityForResult(intent, PictureActivity.GALLERY_KITKAT_INTENT_CALLED)
        }
    }

    private fun getBitmapRotated(path: String, rotation: Int): Bitmap {
        val img = BitmapFactory.decodeFile(path)
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, false)
    }

    private fun flipBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.setScale(-1f, 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }
}