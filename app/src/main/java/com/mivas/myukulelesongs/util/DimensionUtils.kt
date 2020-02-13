package com.mivas.myukulelesongs.util

import android.util.TypedValue
import com.mivas.myukulelesongs.App

/**
 * Handles conversion between pixels and density pixels.
 */
object DimensionUtils {

    /**
     * Converts dp to px.
     *
     * @param dp The dp
     * @return The px
     */
    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), App.instance.applicationContext.resources.displayMetrics).toInt()
    }
}
