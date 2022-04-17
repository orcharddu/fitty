package com.orcharddu.fitty.model

import java.io.Serializable

class DailyEvents(
    private val activityHistory: MutableList<Activity> = mutableListOf(),
    private var calorieIntake: Int = 0,
    val exercisePlans: MutableList<Exercise> = mutableListOf(),
    var challenge: Boolean = false,
    var hintCount: Int = 0,
    var words: MutableList<String> = mutableListOf(),
    var answer: List<String> = listOf(),
) : Serializable {

    init {
        answer = listOf("head", "heal", "teal", "tell", "tall", "tail")
        words = answer.mapIndexed { index, s ->
            if (index == 0 || index == answer.lastIndex) s else ""
        }.toMutableList()
    }

    fun addActivityHistory(activity: Activity) {
        activityHistory.add(0, activity)
        calorieIntake += when (activity.type) {
            Activity.Type.FOOD      -> activity.calorie
            Activity.Type.EXERCISE  -> -activity.calorie
        }

    }

    fun activityHistory(): List<Activity> = activityHistory
    fun calorieIntake() = calorieIntake

}