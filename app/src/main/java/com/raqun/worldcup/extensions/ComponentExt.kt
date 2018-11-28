package com.raqun.labs.extensions

import android.app.WallpaperManager
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView

/**
 * Created by tyln on 7.02.2018.
 */

/**
 * Loads current device wallpaper to an Imageview
 */
fun ImageView.setWallpaper() {
    val wallpaperManager = WallpaperManager.getInstance(context)
    setImageDrawable(wallpaperManager.drawable)
}

/**
 * Shows a Snackbar from a CoordinatorLayout
 */
fun CoordinatorLayout.snackThat(meesage: CharSequence,
                                buttonText: CharSequence,
                                singleShot: View.OnClickListener?) {
    val sb = Snackbar.make(this, meesage, Snackbar.LENGTH_INDEFINITE)
    if (singleShot != null) {
        sb.setAction(buttonText, singleShot)
    } else {
        sb.setAction(buttonText, { sb.dismiss() })
    }
    sb.show()
}

