package com.raqun.worldcup.ui.picture

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.raqun.worldcup.R
import com.raqun.worldcup.ui.BaseActivity
import com.raqun.worldcup.ui.picture.camera.FragmentCamera
import com.raqun.worldcup.ui.picture.edit.FragmentPictureEdit
import com.raqun.worldcup.ui.picture.edit.StickerFragment

/**
 * Created by tyln on 5.05.2018.
 */
class PictureActivity : BaseActivity() {

    private var currentPicture: Bitmap? = null

    override fun getLayoutRes(): Int = R.layout.activity_picture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_main, FragmentCamera())
                    .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.findFragmentById(R.id.fl_main)?.onActivityResult(requestCode, resultCode, data)
    }

    fun setCurrentPicture(picture: Bitmap?) {
        currentPicture = picture
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_main, FragmentPictureEdit())
                .addToBackStack(null)
                .commit()
    }

    fun getCurrentPicture(): Bitmap? = currentPicture

    fun addSticker(image: Bitmap) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fl_main)
        if (fragment is StickerFragment) {
            fragment.addSticker(image)
        }
    }

    companion object {
        const val GALLERY_INTENT_CALLED = 0x1
        const val GALLERY_KITKAT_INTENT_CALLED = 0x2
        //private const val CAMERA_CALLED = 0x3

        private const val PIC_TYPE = "pic_type"

        const val REQUEST_PERMISSION_GRANT_FOR_CAMERA = 1000
        const val REQUEST_PERMISSION_GRANT_FOR_GALLERY = 1001

        val permissions = arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        fun newIntent(context: Context) = Intent(context, PictureActivity::class.java)
    }
}