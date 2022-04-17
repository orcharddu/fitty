package com.orcharddu.fitty.dataholder

import android.content.Context
import com.orcharddu.fitty.model.*
import com.orcharddu.fitty.utils.IO
import com.orcharddu.fitty.utils.Utils
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap


class DataHolder private constructor(
    val dataHolder: MutableMap<String, Any> = ConcurrentHashMap(),
    val user: User,
    private val diary: MutableMap<Long, DailyEvents>
) {

    private class Serializer(
        val user: User = User(
            weights = mutableListOf(50),
            heights = mutableListOf(175),
            useIntegratedCamera = true,
            calorieGoal = 0,
            favorites = mutableListOf()
        ),
        val diary: MutableMap<Long, DailyEvents> = hashMapOf()
    ) : Serializable

    companion object {
        @Volatile private var instance: DataHolder? = null
        private const val FILE_NAME = "data"

        fun init(context: Context): DataHolder {
            return instance ?: synchronized(this) {
                val data = IO.loadSerializedData(context, FILE_NAME) as Serializer? ?: run {
                    Serializer().also {
                        IO.saveSerializedData(context, it, FILE_NAME)
                    }
                }
                instance ?: DataHolder(
                    user = data.user,
                    diary = data.diary,
                ).also {
                    instance = it
                }
            }
        }

        // should be invoked after init
        fun instance(): DataHolder = instance!!
    }

    fun reset() {
        user.apply {
            weights = mutableListOf(50)
            heights = mutableListOf(175)
            useIntegratedCamera = true
            calorieGoal = 0
            favorites = mutableListOf()
        }
        diary.clear()
    }

    fun dailyEvents(date: Long): DailyEvents {
        diary.putIfAbsent(date, DailyEvents())
        return diary[date]!!
    }

    fun dailyEvents(): DailyEvents = dailyEvents(Utils.getCurrentDateInLong())

    fun save(context: Context): Boolean {
        return Serializer(
            user = user,
            diary = diary
        ).let {
            IO.saveSerializedData(context, it, FILE_NAME)
        }
    }

}
