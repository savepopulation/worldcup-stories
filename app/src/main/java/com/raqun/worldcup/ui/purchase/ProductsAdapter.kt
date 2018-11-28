package com.raqun.worldcup.ui.purchase

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.raqun.worldcup.R
import com.raqun.worldcup.inapp.SkuDetails

/**
 * Created by tyln on 7.06.2018.
 */
class ProductsAdapter(private val skus: List<SkuDetails>,
                      private val listener: (sku: SkuDetails) -> Unit) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sku = skus[position]
        holder.bind(sku)
    }

    override fun getItemCount(): Int {
        return skus.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title) as TextView
        private val desc: TextView = itemView.findViewById(R.id.description) as TextView
        private val price: TextView = itemView.findViewById(R.id.price) as TextView

        fun bind(sku: SkuDetails) {
            var titleStr = sku.title
            if (titleStr.contains("(WorldCup Stories)")) {
                titleStr = sku.title.replace("(WorldCup Stories)", "")
            }
            title.text = titleStr
            desc.text = sku.description
            price.text = sku.price
            itemView.setOnClickListener {
                listener.invoke(sku)
            }
        }
    }
}