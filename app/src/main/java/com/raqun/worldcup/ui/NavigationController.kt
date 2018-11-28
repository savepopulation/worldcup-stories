package com.raqun.worldcup.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

/**
 * Created by tyln on 29.01.2018.
 */
class NavigationController(private val activity: FragmentActivity) {

    fun navigate(fragment: Fragment, @IdRes container: Int) {
        activity.supportFragmentManager.commitTransaction {
            replace(container, fragment)
        }
    }

    fun close() {
        activity.finish()
    }

    fun openPurchase() {

    }

    enum class NavigationType {
        ROOT, BACK, EMPTY
    }

    inline fun FragmentManager.commitTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }
}