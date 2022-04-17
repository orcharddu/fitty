package com.orcharddu.fitty.ui

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat


open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    open fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
    }

    open fun getAppBarHeight() = let {
        getStatusBarHeight() + if (theme.resolveAttribute(android.R.attr.windowTitleSize, TypedValue(), true))
            TypedValue.complexToDimensionPixelSize(TypedValue().data, resources.displayMetrics) else 0
    }
}