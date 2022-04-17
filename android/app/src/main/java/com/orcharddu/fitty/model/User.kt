package com.orcharddu.fitty.model

import java.io.Serializable

class User(
    var weights: MutableList<Int> = mutableListOf(),
    var heights: MutableList<Int> = mutableListOf(),
    var useIntegratedCamera: Boolean = true,
    var calorieGoal: Int = 0,
    var favorites: MutableList<Food> = mutableListOf()
) : Serializable

