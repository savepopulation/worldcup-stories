package com.raqun.worldcup

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.raqun.worldcup.data.ProductData
import com.raqun.worldcup.model.Product
import com.raqun.worldcup.util.SharedPrefUtil

/**
 * Created by tyln on 5.05.2018.
 */
class WorldCupApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Init product data
        ProductData.hasAll = SharedPrefUtil.get(this, Product.ALL.key, false)
        ProductData.hasClothes = SharedPrefUtil.get(this, Product.CLOTHES.key, false)
        ProductData.hasCelebration = SharedPrefUtil.get(this, Product.CELEBRATION.key, false)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}