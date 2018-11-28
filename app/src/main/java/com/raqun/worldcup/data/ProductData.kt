package com.raqun.worldcup.data

import com.raqun.worldcup.model.Product

/**
 * Created by tyln on 8.06.2018.
 */
object ProductData {

    var hasClothes: Boolean = false
    var hasCelebration: Boolean = false
    var hasAll: Boolean = false

    fun initPurchase(key: String, b: Boolean) {
        when (key) {
            Product.ALL.key -> hasAll = b
            Product.CLOTHES.key -> hasClothes = b
            Product.CELEBRATION.key -> hasCelebration = b
        }

        CountriesData.updateStickerLocks()
    }

    fun hasAny() = hasAll || hasClothes || hasCelebration

}