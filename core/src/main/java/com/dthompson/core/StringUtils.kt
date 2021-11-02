package com.dthompson.core

import android.content.Context
import androidx.annotation.PluralsRes

class StringUtils {
    companion object {
        fun getPriceString(price: Int, @PluralsRes resId: Int, context: Context): String {
            return if (price <= 2) {
                context.resources.getQuantityString(resId, price)
            } else {
                // Max price indicator is 4, so use that to determine the right plural string to use.
                context.resources.getQuantityString(resId, 4 - price)
            }
        }
    }
}