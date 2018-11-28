package com.raqun.worldcup.ui

import android.app.AlertDialog
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.raqun.worldcup.BuildConfig
import com.raqun.worldcup.Constants
import com.raqun.worldcup.data.ProductData
import com.raqun.worldcup.inapp.IabBroadcastReceiver
import com.raqun.worldcup.inapp.IabHelper
import com.raqun.worldcup.inapp.Inventory
import com.raqun.worldcup.model.Product
import com.raqun.worldcup.util.SharedPrefUtil


/**
 * Created by tyln on 6.06.2018.
 */
abstract class BaseInAppActivity : BaseActivity(), IabBroadcastReceiver.IabBroadcastListener {

    protected var mHelper: IabHelper? = null

    abstract fun inAppSetupFinish(inventory: Inventory)

    // Listener that's called when we finish querying the items and subscriptions we own
    private var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener = IabHelper.QueryInventoryFinishedListener { result, inventory ->
        //Log.d(TAG, "Query inventory finished.");

        // Have we been disposed of in the meantime? If so, quit.
        if (mHelper == null) return@QueryInventoryFinishedListener

        // Is it a failure?
        if (result.isFailure) {
            complain("Failed to query inventory: " + result)
            return@QueryInventoryFinishedListener
        }

        // Log.d(TAG, "Query inventory was successful.");

        /*
         * Check for items we own. Notice that for each purchase, we check
         * the developer payload to see if it's correct! See
         * verifyDeveloperPayload().
         */

        //            // Do we have the premium upgrade?
        //            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
        //            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
        //            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

        // First find out which subscription is auto renewing
        /*
        val clothes = inventory.getPurchase(SKU_CLOTHES)
        val celebration = inventory.getPurchase(SKU_CELEBRATION)
        val all = inventory.getPurchase(SKU_ALL)

        if (clothes != null && clothes.isAutoRenewing) {
            mBoughtSkuKey = SKU_CLOTHES
            //  Log.d(TAG, "onQueryInventoryFinished: monthly");
        } else if (celebration != null && celebration.isAutoRenewing) {
            mBoughtSkuKey = SKU_CELEBRATION
            // Log.d(TAG, "onQueryInventoryFinished: yearly");
        } else if (all != null && all.isAutoRenewing) {
            // Log.d(TAG, "onQueryInventoryFinished: 6 months");
            mBoughtSkuKey = SKU_ALL
        }

        // The user is subscribed if either subscription exists, even if neither is auto
        // renewing
        //mSubscribed = monthly != null || yearly != null || premium6Months != null
        // Log.d(TAG, "User " + (mSubscribed ? "HAS" : "DOES NOT HAVE")
        //       + " infinite  subscription.");
        //SharedPrefManager.getInstance().savePremium(mSubscribed)
        */

        val allProducts = inventory.getPurchase(Product.ALL.key)
        val hasAllProducts = allProducts != null
        ProductData.hasAll = hasAllProducts
        SharedPrefUtil.put(this, Product.ALL.key, hasAllProducts)

        val clothes = inventory.getPurchase(Product.CLOTHES.key)
        val hasClothes = clothes != null
        ProductData.hasClothes = hasClothes
        SharedPrefUtil.put(this, Product.CLOTHES.key, hasClothes)

        val celebration = inventory.getPurchase(Product.CELEBRATION.key)
        val hasCelebration = celebration != null
        ProductData.hasCelebration = hasCelebration
        SharedPrefUtil.put(this, Product.CELEBRATION.key, hasCelebration)

        inAppSetupFinish(inventory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //        Log.d(TAG, "Creating IAB helper.");
        mHelper = IabHelper(this, Constants.BASE64_ENCODE_PUBLIC_KEY)

        // enable debug logging (for a production application, you should set this to false).
        if (BuildConfig.DEBUG) {
            mHelper?.enableDebugLogging(true)
        }

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.

        mHelper?.startSetup(IabHelper.OnIabSetupFinishedListener { result ->
            Log.d("Splash", "Setup finished.")

            if (!result.isSuccess) {
                // Oh noes, there was a problem.
                complain("Problem setting up in-app billing: " + result)
                return@OnIabSetupFinishedListener
            }

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return@OnIabSetupFinishedListener

            // Important: Dynamically register for broadcast messages about updated purchases.
            // We register the receiver here instead of as a <receiver> in the Manifest
            // because we always call getPurchases() at startup, so therefore we can ignore
            // any broadcasts sent while the app isn't running.
            // Note: registering this listener in an Activity is a bad idea, but is done here
            // because this is a SAMPLE. Regardless, the receiver must be registered after
            // IabHelper is setup, but before first call to getPurchases().
            //                mBroadcastReceiver = new IabBroadcastReceiver(BaseInAppActivity.this);
            //                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
            //                registerReceiver(mBroadcastReceiver, broadcastFilter);


            val broadcastFilter = IntentFilter(IabBroadcastReceiver.ACTION)
            LocalBroadcastManager.getInstance(this).registerReceiver(IabBroadcastReceiver(this), broadcastFilter)

            // IAB is fully set up. Now, let's get an inventory of stuff we own
            Log.d("Splash", "Setup successful. Querying inventory.")
            try {
                val list = ArrayList<String>()
                list.add(Product.CLOTHES.key)
                list.add(Product.CELEBRATION.key)
                list.add(Product.ALL.key)
                mHelper?.queryInventoryAsync(true, list, list, mGotInventoryListener)
            } catch (e: IabHelper.IabAsyncInProgressException) {
                complain("Error querying inventory. Another async operation in progress.")
            }
        })
    }

    private fun complain(message: String) {
        // Log.e(TAG, "**** Instatracker Error: " + message);
        alert("Error: " + message)
    }

    private fun alert(message: String) {
        if (isFinishing || isDestroyed) {
            return
        }
        val bld = AlertDialog.Builder(this)
        bld.setMessage(message)
        bld.setNeutralButton("OK", null)
        Log.d("Splash", "Showing alert dialog: " + message)
        bld.create().show()
    }

    public override fun onDestroy() {

        // very important:
        // Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper?.disposeWhenFinished()
            mHelper = null
        }
        super.onDestroy()
    }


    override fun receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        // Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {

            mHelper?.queryInventoryAsync(mGotInventoryListener)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            complain("Error querying inventory. Another async operation in progress.")
        }

    }

}