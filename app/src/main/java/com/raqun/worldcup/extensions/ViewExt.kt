package com.raqun.labs.extensions

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_stickers.*


/**
 * Created by tyln on 6.04.2018.
 */
fun View.initVisiblity(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun RecyclerView.initHorizontal(activity: Activity) {
    val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    setLayoutManager(layoutManager)
}

fun RecyclerView.init(activity: Activity) {
    val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    setLayoutManager(layoutManager)
}