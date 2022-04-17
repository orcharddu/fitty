package com.orcharddu.fitty.model

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import com.orcharddu.fitty.dataholder.Resources
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
interface Activity : Serializable {
    enum class Type { FOOD, EXERCISE }
    var type: Type
    var name: String
    var calorie: Int
    var icon: Resources.Icon
    var time: String
    var date: String

    var timeFormatter: SimpleDateFormat
        get() = SimpleDateFormat("HH:mm")
        set(_) {}
    var dateFormatter: SimpleDateFormat
        get() = SimpleDateFormat("MM-dd")
        set(_) {}
    var now: Date
        get() = Date(System.currentTimeMillis())
        set(_) { }
}