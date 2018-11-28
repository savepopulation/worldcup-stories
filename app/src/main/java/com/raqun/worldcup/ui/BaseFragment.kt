package com.raqun.worldcup.ui

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.MenuRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.Toolbar
import android.view.*
import com.raqun.worldcup.Constants
import com.raqun.worldcup.R

/**
 * Created by tyln on 29.01.2018.
 */
abstract class BaseFragment : Fragment() {

    protected var navigationController: NavigationController? = null

    @LayoutRes
    protected abstract fun getLayoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationController = NavigationController(activity)
        if (getMenuRes() != Constants.NO_RES) {
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        if (getMenuRes() != Constants.NO_RES) {
            inflater?.inflate(getMenuRes(), menu)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(getLayoutRes(), null, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (getTitleRes() != Constants.NO_RES) {
            setActivityTitle(getString(getTitleRes()))
        }
    }

    override fun onDestroyView() {
        navigationController = null
        super.onDestroyView()
    }

    protected fun setActivityTitle(title: String) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).setScreenTitle(title)
        }
    }

    @MenuRes
    protected open fun getMenuRes(): Int = Constants.NO_RES

    @StringRes
    protected open fun getTitleRes(): Int = R.string.app_name

    fun getApplication(): Application = activity.application

    fun getApplicationContext(): Context = getApplication().applicationContext
}