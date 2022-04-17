package com.orcharddu.fitty.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View

object UIUtils {


//        @JvmStatic
//        open fun getStatusBarHeight(): Int {
//            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
//            return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
//            else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
//        }
//
//        @JvmStatic
//        open fun getAppBarHeight() = let {
//            getStatusBarHeight() + if (theme.resolveAttribute(android.R.attr.windowTitleSize, TypedValue(), true))
//                TypedValue.complexToDimensionPixelSize(TypedValue().data, resources.displayMetrics) else 0
//        }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun drawableWithColorFilter(context: Context, drawable: Drawable, colorID: Int): Drawable {
        drawable.colorFilter = PorterDuffColorFilter(context.getColor(colorID), PorterDuff.Mode.SRC_ATOP)
        return drawable
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun drawableWithColorFilter(context: Context, ID: Int, colorID: Int): Drawable {
        val drawable: Drawable = context.getDrawable(ID)!!
        drawable.colorFilter =
            PorterDuffColorFilter(context.getColor(colorID), PorterDuff.Mode.SRC_ATOP)
        return drawable
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun drawableWithColorFilter(context: Context, ID: Int, colorString: String): Drawable {
        val drawable: Drawable = context.getDrawable(ID)!!
        drawable.colorFilter =
            PorterDuffColorFilter(Color.parseColor(colorString), PorterDuff.Mode.SRC_ATOP)
        return drawable
    }

//        fun getActivityScreenShot(activity: BaseActivity): Bitmap? {
//            val view: View = activitygetWindow().getDecorView()
//            view.isDrawingCacheEnabled = true
//            view.buildDrawingCache()
//            val bitmap = Bitmap.createBitmap(
//                view.drawingCache, 0, 0,
//                getRealDisplayMetrics().widthPixels,
//                getUserDisplayMetrics().heightPixels + getStatusBarHeight()
//            )
//            view.destroyDrawingCache()
//            view.isDrawingCacheEnabled = false
//            return bitmap
//        }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        return bitmap
    }

    fun getBitmapFromView(activity: Activity): Bitmap {
        activity.apply {
            val view = window.decorView.rootView
            return Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also {
                view.draw(Canvas(it))
            }
        }
    }

    fun getColorDrawable(color: String): Drawable {
        return ColorDrawable(Color.parseColor(color))
    }

}