package com.raqun.worldcup.ui.picture.edit

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.raqun.worldcup.R
import com.raqun.worldcup.data.CountriesData
import com.raqun.worldcup.model.Country
import com.raqun.worldcup.util.ImageUtil
import com.squareup.picasso.Picasso

/**
 * Created by tyln on 6.06.2018.
 */
class CountriesAdapter(val listener: (country: Country) -> Unit) : RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = CountriesData.countries[position]
        holder.bind(country)
    }

    override fun getItemCount(): Int {
        return CountriesData.countries.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.logo) as ImageView

        fun bind(country: Country) {
            val logoResId = itemView.context.resources.getIdentifier(country.logoResIdStr, "drawable", imageView.context.packageName)
            //imageView.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(itemView.context.resources,
                    //logoResId, 144, 144))
            Picasso.get().load(logoResId)
                    .resize(144, 144)
                    .onlyScaleDown()
                    .centerInside()
                    .into(imageView)

            imageView.setOnClickListener {
                listener.invoke(country)
            }
        }
    }
}