package com.mivas.myukulelesongs.util

import android.util.TypedValue
import com.mivas.myukulelesongs.App

object DimensionUtils {

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), App.instance.applicationContext.resources.displayMetrics).toInt()
    }
}
