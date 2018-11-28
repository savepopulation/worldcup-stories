package com.raqun.worldcup.ui.purchase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.raqun.labs.extensions.init
import com.raqun.worldcup.BuildConfig
import com.raqun.worldcup.R
import com.raqun.worldcup.data.CountriesData
import com.raqun.worldcup.data.ProductData
import com.raqun.worldcup.inapp.*
import com.raqun.worldcup.ui.BaseActivity
import com.raqun.worldcup.ui.BaseInAppActivity
import com.raqun.worldcup.util.SharedPrefUtil
import kotlinx.android.synthetic.main.activity_purchase.*


/**
 * Created by tyln on 6.06.2018.
 */
class PurchaseActivity : BaseInAppActivity() {

    override fun getLayoutRes(): Int = R.layout.activity_purchase

    companion object {
        const val REQUEST_CODE = 10001
        const val PURCHASE_REQUEST_CODE = 1002

        fun newIntent(context: Context) = Intent(context, PurchaseActivity::class.java)
    }

    override fun inAppSetupFinish(inventory: Inventory) {
        val allSkus = inventory.getAllAvailableSkus(IabHelper.ITEM_TYPE_INAPP)
        products.init(this)
        products.adapter = ProductsAdapter(allSkus) {
            click(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScreenTitle(getString(R.string.title_purchase))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (!mHelper!!.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun click(skudetail: SkuDetails) {
        try {
            mHelper?.launchPurchaseFlow(this, skudetail.sku, REQUEST_CODE) { result, _ ->
                if (result.isSuccess) {
                    SharedPrefUtil.put(this, skudetail.sku, result.isSuccess)
                    ProductData.initPurchase(skudetail.sku, result.isSuccess)
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

        } catch (e: IabHelper.IabAsyncInProgressException) {
            e.printStackTrace()
        }
    }
}