package com.raqun.worldcup.util

import android.view.View

/**
 * Created by mertsimsek on 11/02/18.
 */
object ViewUtil {
    fun addClickListener(listener: View.OnClickListener, vararg views: View) {
        for (item: View in views) {
            item.setOnClickListener(listener)
        }
    }
}