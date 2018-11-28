package com.raqun.worldcup.data

import android.util.Log
import com.raqun.worldcup.model.Country
import com.raqun.worldcup.model.Sticker
import com.raqun.worldcup.model.StickerType

/**
 * Created by tyln on 6.06.2018.
 */
object CountriesData {

    val countries: ArrayList<Country> = ArrayList()
    var lastSelectedCountry: Country

    init {
        countries.add(Country("",
                logoNum = 57,
                celebrationMın = 15,
                celebrationMax = 58,
                clothesMın = 0,
                clothesMax = 0))

        countries.add(Country("ar"))
        countries.add(Country("au"))
        countries.add(Country("be"))
        countries.add(Country("br"))
        countries.add(Country("co"))
        countries.add(Country("cr"))
        countries.add(Country("hr"))
        countries.add(Country("dk"))
        countries.add(Country("eg"))
        countries.add(Country("eng"))
        countries.add(Country("fr"))
        countries.add(Country("de"))
        countries.add(Country("is"))
        countries.add(Country("ir"))
        countries.add(Country("jp"))
        countries.add(Country("mx"))
        countries.add(Country("ma"))
        countries.add(Country("ng"))
        countries.add(Country("pa"))
        countries.add(Country("pe"))
        countries.add(Country("pl"))
        countries.add(Country("pt"))
        countries.add(Country("ru"))
        countries.add(Country("sa"))
        countries.add(Country("sn"))
        countries.add(Country("rs"))
        countries.add(Country("kr"))
        countries.add(Country("es"))
        countries.add(Country("se"))
        countries.add(Country("ch"))
        countries.add(Country("tn"))
        countries.add(Country("uy"))

        initStickers()

        lastSelectedCountry = countries[0]
    }

    private fun initStickers() {
        for (country in countries) {
            for (i in country.clothesMın until country.clothesMax) {
                country.stickers.add(Sticker(StickerType.CLOTHES, i, country.prefix))
            }

            for (i in country.celebrationMın until country.celebrationMax) {
                country.stickers.add(Sticker(StickerType.CELEBRATION, i, country.prefix))
            }
        }
    }

    fun updateStickerLocks() {
        countries
                .flatMap { it.stickers }
                .forEach { it.initLock() }
    }
}