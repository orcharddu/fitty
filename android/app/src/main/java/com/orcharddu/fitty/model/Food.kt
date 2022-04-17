package com.orcharddu.fitty.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.orcharddu.fitty.dataholder.Resources
import java.io.Serializable


data class Food(
    override var type: Activity.Type = Activity.Type.FOOD,
    override var name: String = "",
    override var calorie: Int = 1000,
    override var icon: Resources.Icon = Resources.Icon.FOOD,
    override var time: String = "",
    override var date: String = "",
    var weights: Int = 100,
    var size: MealSize = MealSize.MEDIUM,
    var picture: ByteArray? = null,
    var favorite: Boolean = false
) : Activity {
    enum class MealSize(val sizeFactor: Float) : Serializable {
        SMALL(1f), MEDIUM(2f), LARGE(3f);
    }
}

