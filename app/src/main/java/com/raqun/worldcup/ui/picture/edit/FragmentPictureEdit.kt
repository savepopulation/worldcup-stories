package com.raqun.worldcup.ui.picture.edit

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.View
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK
import com.raqun.worldcup.R
import com.raqun.worldcup.ui.BaseFragment
import com.raqun.worldcup.ui.widget.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.fragment_edit.*
import android.view.Gravity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.content.Intent
import android.net.Uri
import android.widget.*
import com.raqun.labs.extensions.initVisiblity
import com.raqun.worldcup.data.ProductData
import com.raqun.worldcup.ui.BaseInAppActivity
import com.raqun.worldcup.ui.picture.PictureActivity
import com.raqun.worldcup.ui.purchase.PurchaseActivity
import com.raqun.worldcup.util.AlertUtil
import com.raqun.worldcup.util.ImageUtil
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by tyln on 4.06.2018.
 */
class FragmentPictureEdit : BaseFragment(), StickerFragment {

    private var photoEditorSDK: PhotoEditorSDK? = null
    private var colorPickerColors = ArrayList<Int>()
    private var colorCodeTextView = -1
    private var iv_watermark: ImageView? = null

    override fun getLayoutRes(): Int = R.layout.fragment_edit

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWaterMark()

        photoEditorSDK = PhotoEditorSDK.PhotoEditorSDKBuilder(activity)
                .parentView(parent_image_rl)
                .childView(photo_edit_iv)
                .deleteView(delete_rl)
                .brushDrawingView(drawing_view)
                .buildPhotoEditorSDK()

        initColorPickerColors()

        emoji.setOnClickListener {
            StickersFragment.newInstance().show(childFragmentManager, StickersFragment.TAG)
            //sliding_layout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED;
        }

        close.setOnClickListener {
            activity.onBackPressed()
        }

        clear.setOnClickListener {
            try {
                photoEditorSDK?.clearAllViews()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        text.setOnClickListener {
            openAddTextPopupWindow("", colorCodeTextView)
        }

        brush.setOnClickListener {
            updateBrushDrawingView(true)
        }

        erase.setOnClickListener {
            photoEditorSDK?.brushEraser()
        }

        done.setOnClickListener {
            updateBrushDrawingView(false)
        }

        save.setOnClickListener {
            val path = saveImage()
            path?.let {
                AlertUtil.alert(activity, "Your picture saved successfully!!")
            } ?: AlertUtil.alert(activity, "An unknown error has been occurred!!")
        }

        share.setOnClickListener {
            share()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        photo_edit_iv.setImageBitmap((activity as PictureActivity).getCurrentPicture())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PurchaseActivity.PURCHASE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                parent_image_rl.removeView(iv_watermark)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initWaterMark() {
        if (!ProductData.hasAny()) {
            iv_watermark = ImageView(context)
            iv_watermark?.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(context.resources,
                    R.mipmap.ic_launcher, 144, 144))
            iv_watermark?.alpha = 0.2f
            parent_image_rl.addView(iv_watermark)
            val params = iv_watermark?.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            params.topMargin = resources.getDimensionPixelOffset(R.dimen.watermark_top_margin)
            params.rightMargin = resources.getDimensionPixelOffset(R.dimen.watermark_right_margin)
            params.height = resources.getDimensionPixelOffset(R.dimen.watermark_height)
            params.width = resources.getDimensionPixelOffset(R.dimen.watermark_width)
            iv_watermark!!.layoutParams = params
            iv_watermark!!.setOnClickListener {
                startActivityForResult(PurchaseActivity.newIntent(activity), PurchaseActivity.PURCHASE_REQUEST_CODE)
            }
        }
    }

    private fun addImage(image: Bitmap) {
        photoEditorSDK?.addImage(image)
        if (sliding_layout != null)
            sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    override fun addSticker(image: Bitmap) {
        addImage(image)
    }

    private fun initColorPickerColors() {
        colorPickerColors.add(resources.getColor(R.color.black))
        colorPickerColors.add(resources.getColor(R.color.blue_color_picker))
        colorPickerColors.add(resources.getColor(R.color.brown_color_picker))
        colorPickerColors.add(resources.getColor(R.color.green_color_picker))
        colorPickerColors.add(resources.getColor(R.color.orange_color_picker))
        colorPickerColors.add(resources.getColor(R.color.red_color_picker))
        colorPickerColors.add(resources.getColor(R.color.red_orange_color_picker))
        colorPickerColors.add(resources.getColor(R.color.sky_blue_color_picker))
        colorPickerColors.add(resources.getColor(R.color.violet_color_picker))
        colorPickerColors.add(resources.getColor(R.color.white))
        colorPickerColors.add(resources.getColor(R.color.yellow_color_picker))
        colorPickerColors.add(resources.getColor(R.color.yellow_green_color_picker))
    }

    private fun openAddTextPopupWindow(text: String, colorCode: Int) {
        colorCodeTextView = colorCode
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val addTextPopupWindowRootView = inflater.inflate(R.layout.add_text_popup_window, null)
        val addTextEditText = addTextPopupWindowRootView.findViewById(R.id.add_text_edit_text) as EditText
        val addTextDoneTextView = addTextPopupWindowRootView.findViewById(R.id.add_text_done_tv) as TextView
        val addTextColorPickerRecyclerView = addTextPopupWindowRootView.findViewById(R.id.add_text_color_picker_recycler_view) as RecyclerView
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.layoutManager = layoutManager
        addTextColorPickerRecyclerView.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(activity, colorPickerColors)
        colorPickerAdapter.setOnColorPickerClickListener(object : ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                addTextEditText.setTextColor(colorCode)
                colorCodeTextView = colorCode
            }
        })

        addTextColorPickerRecyclerView.adapter = colorPickerAdapter
        if (!TextUtils.isEmpty(text)) {
            addTextEditText.setText(text)
            addTextEditText.setTextColor(if (colorCode == -1) resources.getColor(R.color.white) else colorCode)
        }
        val pop = PopupWindow(activity)
        pop.contentView = addTextPopupWindowRootView
        pop.width = LinearLayout.LayoutParams.MATCH_PARENT
        pop.height = LinearLayout.LayoutParams.MATCH_PARENT
        pop.isFocusable = true
        pop.setBackgroundDrawable(null)
        pop.showAtLocation(addTextPopupWindowRootView, Gravity.TOP, 0, 0)
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        addTextDoneTextView.setOnClickListener { view ->
            addText(addTextEditText.text.toString(), colorCodeTextView)
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            pop.dismiss()
        }
    }

    private fun updateBrushDrawingView(brushDrawingMode: Boolean) {
        photoEditorSDK?.setBrushDrawingMode(brushDrawingMode)
        if (brushDrawingMode) {
            drawing_view_color_picker_recycler_view.visibility = View.VISIBLE
            rl_brush_mode.visibility = View.VISIBLE
            top_parent_rl.visibility = View.GONE
            bottom_parent_rl.visibility = View.GONE
            val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            drawing_view_color_picker_recycler_view.setLayoutManager(layoutManager)
            drawing_view_color_picker_recycler_view.setHasFixedSize(true)
            val colorPickerAdapter = ColorPickerAdapter(activity, colorPickerColors)
            colorPickerAdapter.setOnColorPickerClickListener(object : ColorPickerAdapter.OnColorPickerClickListener {
                override fun onColorPickerClickListener(colorCode: Int) {
                    photoEditorSDK?.brushColor = colorCode
                }
            })
            drawing_view_color_picker_recycler_view.adapter = colorPickerAdapter
        } else {
            drawing_view_color_picker_recycler_view.visibility = View.GONE
            rl_brush_mode.visibility = View.GONE
            top_parent_rl.visibility = View.VISIBLE
            bottom_parent_rl.visibility = View.VISIBLE
        }
    }

    private fun addText(text: String, colorCodeTextView: Int) {
        photoEditorSDK?.addText(text, colorCodeTextView)
    }

    private fun saveImage(): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "WCS_IMG_$timeStamp.jpg"
        return photoEditorSDK?.saveImage("WorldCupStories", imageName)
    }

    private fun share() {
        val path = saveImage()
        path?.let {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            share.putExtra(Intent.EXTRA_STREAM, (Uri.parse(path)))
            startActivity(Intent.createChooser(share, "Share via"))
        } ?: AlertUtil.alert(activity, "Sorry! We can not share image right now.")
    }
}