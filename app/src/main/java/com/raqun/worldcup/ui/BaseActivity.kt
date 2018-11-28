package com.raqun.worldcup.ui

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.MenuRes
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.raqun.worldcup.Constants
import com.raqun.worldcup.R

/**
 * Created by tyln on 29.01.2018.
 */
abstract class BaseActivity : AppCompatActivity() {

    @LayoutRes
    abstract fun getLayoutRes(): Int

    protected var navigationController: NavigationController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        initNavigation(getNavigationType())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (getMenuRes() != Constants.NO_RES) {
            menuInflater.inflate(getMenuRes(), menu)
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    @MenuRes
    protected open fun getMenuRes(): Int = Constants.NO_RES

    protected open fun getNavigationType() = NavigationController.NavigationType.BACK

    fun setScreenTitle(title: String?) {
        supportActionBar?.title = title ?: getString(R.string.app_name)
    }

    fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    private fun initNavigation(navigationType: NavigationController.NavigationType) {
        when (navigationType) {
            NavigationController.NavigationType.BACK -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
            NavigationController.NavigationType.ROOT -> supportActionBar?.setDisplayHomeAsUpEnabled(false)
            else -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        navigationController = null
        super.onDestroy()
    }
}
