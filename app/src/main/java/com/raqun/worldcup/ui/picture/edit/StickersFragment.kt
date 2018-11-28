package com.raqun.worldcup.ui.picture.edit

import android.app.Dialog
import android.view.ViewTreeObserver
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.raqun.labs.extensions.initHorizontal
import com.raqun.worldcup.R
import com.raqun.worldcup.data.CountriesData
import com.raqun.worldcup.model.Sticker
import com.raqun.worldcup.ui.picture.PictureActivity
import com.raqun.worldcup.ui.purchase.PurchaseActivity
import com.raqun.worldcup.util.ImageUtil


/**
 * Created by tyln on 6.06.2018.
 */
class StickersFragment : BottomSheetDialogFragment() {

    private val stickers: ArrayList<Sticker> = ArrayList()
    private lateinit var stickersAdapter: StickersAdapter

    private val mBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Empty
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stickers.addAll(CountriesData.lastSelectedCountry.stickers)
        stickersAdapter = StickersAdapter(stickers, { sticker ->
            val stickerResId = resources.getIdentifier(sticker.stickerResId, "drawable", context.packageName)
            val bitmap = (ImageUtil.decodeSampledBitmapFromResource(resources,
                    stickerResId, 180, 180))

            if (sticker.isLocked) {
                activity.startActivityForResult(PurchaseActivity.newIntent(activity), PurchaseActivity.PURCHASE_REQUEST_CODE)
            } else {
                (activity as PictureActivity).addSticker(bitmap)
            }
            dismissAllowingStateLoss()
        })
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = LayoutInflater.from(activity)
                .inflate(R.layout.fragment_stickers, null)
        initView(contentView)
        dialog.setContentView(contentView)

        val params = (contentView.parent as View)
                .layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior!!.setBottomSheetCallback(mBottomSheetCallback)
            contentView.viewTreeObserver
                    .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            bottomSheetBehavior.peekHeight = contentView.measuredHeight
                        }
                    })
        }
    }

    private fun initView(view: View?) {

        view?.findViewById<RecyclerView>(R.id.rv_stickers)?.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = stickersAdapter
        }

        view?.findViewById<RecyclerView>(R.id.rv_countries)?.apply {
            initHorizontal(activity)
            adapter = CountriesAdapter({ country ->
                CountriesData.lastSelectedCountry = country
                stickers.clear()
                stickers.addAll(country.stickers)
                stickersAdapter.notifyDataSetChanged()
            })
        }
    }

    companion object {
        const val TAG = "StickersFragment"

        fun newInstance() = StickersFragment()
    }
}