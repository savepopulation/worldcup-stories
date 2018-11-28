package com.raqun.worldcup.model

/**
 * Created by tyln on 6.06.2018.
 */
data class Country(val prefix: String,
                   private val logoNum: Int = 13,
                   var logoResIdStr: String = "",
                   val celebrationMın: Int = 1,
                   val celebrationMax: Int = 14,
                   val clothesMın: Int = 1,
                   val clothesMax: Int = 25,
                   val stickers: ArrayList<Sticker> = ArrayList()) {
    init {
        logoResIdStr = prefix + StickerType.CELEBRATION.prefix + logoNum
    }
}