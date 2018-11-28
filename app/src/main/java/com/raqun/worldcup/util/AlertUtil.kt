package com.raqun.worldcup.util

import android.content.Context
import android.widget.Toast

/**
 * Created by tyln on 26.05.2018.
 */
class AlertUtil {
    companion object {
        fun alert(context: Context, message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}