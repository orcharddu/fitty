package com.orcharddu.fitty.dataholder

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.orcharddu.fitty.R
import java.io.Serializable

class Resources private constructor(
    val icons: Map<Icon, Drawable?>
) {
    enum class Icon : Serializable {
        FOOD, SPORT, JOGGING, RIDING, ROWING, BASKETBALL, SOCCER, TABLE_TENNIS, TENNIS
    }
    companion object {
        @Volatile private var instance: Resources? = null
        fun init(context: Context): Resources {
            return instance ?: synchronized(this) {
                val icons = mapOf(
                    Pair(Icon.FOOD, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_activity_food, null)),
                    Pair(Icon.SPORT, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_activity_sport, null)),
                    Pair(Icon.JOGGING, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_exercise_jogging, null)),
                    Pair(Icon.RIDING, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_exercise_riding, null)),
                    Pair(Icon.ROWING, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_exercise_rowing, null)),
                    Pair(Icon.BASKETBALL, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_exercise_basketball, null)),
                    Pair(Icon.SOCCER, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_exercise_soccer, null)),
                    Pair(Icon.TABLE_TENNIS, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_exercise_tabletennis, null)),
                    Pair(Icon.TENNIS, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_exercise_tennis, null)),
                )
                instance ?: Resources(
                    icons = icons
                ).also {
                    instance = it
                }
            }
        }

        // should be invoked after init
        fun instance(): Resources = instance!!
    }

}