package com.orcharddu.fitty.model

import android.graphics.drawable.Drawable
import com.orcharddu.fitty.dataholder.Resources

data class Exercise(
    override var type: Activity.Type = Activity.Type.EXERCISE,
    override var name: String = "",
    override var calorie: Int = 1000,
    override var icon: Resources.Icon = Resources.Icon.SPORT,
    override var time: String = "",
    override var date: String = "",
    var duration: Int = 10,
    var calorieFactor: Double = 1.0,
    var checked: Boolean = false,
) : Activity

