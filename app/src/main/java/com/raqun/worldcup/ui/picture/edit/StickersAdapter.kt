package com.raqun.worldcup.ui.picture.edit

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.raqun.labs.extensions.initVisiblity
import com.raqun.worldcup.R
import com.raqun.worldcup.model.Sticker
import com.raqun.worldcup.util.ImageUtil
import com.squareup.picasso.Picasso

/**
 * Created by tyln on 6.06.2018.
 */
class StickersAdapter(private val stickers: ArrayList<Sticker>,
                      private val listener: (sticker: Sticker) -> Unit) : RecyclerView.Adapter<StickersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sticker, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sticker = stickers[position]
        holder.bind(sticker)
    }

    override fun getItemCount(): Int {
        return stickers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.sticker) as ImageView
        private val imageViewLock: ImageView = itemView.findViewById(R.id.lock) as ImageView

        fun bind(sticker: Sticker) {
            val stickerResId = itemView.context.resources.getIdentifier(sticker.stickerResId, "drawable", imageView.context.packageName)

            /*imageView.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(itemView.context.resources,
                    stickerResId, 120, 120))*/

            Picasso.get().load(stickerResId)
                    .resize(540, 540)
                    .onlyScaleDown()
                    .centerInside()
                    .into(imageView)

            imageView.setOnClickListener {
                listener.invoke(sticker)
            }

            imageViewLock.initVisiblity(sticker.isLocked)
        }
    }
}