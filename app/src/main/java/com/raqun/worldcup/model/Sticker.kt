package com.raqun.worldcup.model

import android.text.TextUtils
import com.raqun.worldcup.data.ProductData

/**
 * Created by tyln on 6.06.2018.
 */
data class Sticker(val type: StickerType,
                   private val pos: Int,
                   private val countryPrefix: String,
                   var stickerResId: String = "",
                   var isLocked: Boolean = true) {
    init {
        val positionStr = if (pos < 10) "0$pos" else pos
        stickerResId = countryPrefix + type.prefix + positionStr
        isLocked = getLock()
    }

    fun initLock() {
        isLocked = getLock()
    }

    private fun getLock(): Boolean {
        if (TextUtils.isEmpty(countryPrefix)) {
            return false
        }

        if (ProductData.hasAll) {
            return false
        }

        if (type == StickerType.CLOTHES && ProductData.hasClothes) {
            return false
        }

        if (type == StickerType.CELEBRATION && ProductData.hasCelebration) {
            return false
        }

        if (pos == 1 || pos == 6 || pos == 9) {
            return false
        }

        return true
    }
}