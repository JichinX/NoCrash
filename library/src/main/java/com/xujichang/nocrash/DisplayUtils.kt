package com.xujichang.nocrash

import android.content.Context
import android.content.res.Configuration

object DisplayUtils {

    fun collectInfo(context: Context): String =
        """
         ${getWidth(context)} x ${getHeight(context)} ${getDpi(context)}
         ${if (isHorizontal(context)) "横屏" else "竖屏"} 
        """.trimIndent()

    private fun isHorizontal(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun getDpi(context: Context): Int = context.resources.configuration.densityDpi

    private fun getHeight(context: Context): Int =
        context.resources.displayMetrics.heightPixels

    private fun getWidth(context: Context): Int = context.resources.displayMetrics.widthPixels
}