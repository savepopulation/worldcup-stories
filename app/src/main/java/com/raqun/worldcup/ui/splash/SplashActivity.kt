package com.raqun.worldcup.ui.splash

import android.os.Bundle
import com.raqun.worldcup.BuildConfig
import com.raqun.worldcup.R
import com.raqun.worldcup.inapp.Inventory
import com.raqun.worldcup.ui.BaseInAppActivity
import com.raqun.worldcup.ui.picture.PictureActivity
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * Created by tyln on 6.06.2018.
 */
class SplashActivity : BaseInAppActivity() {

    override fun getLayoutRes(): Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv_version.text = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
    }

    override fun inAppSetupFinish(inventory: Inventory) {
        startActivity(PictureActivity.newIntent(this))
        finish()
    }
}