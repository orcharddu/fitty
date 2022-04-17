package com.orcharddu.fitty.ui.recorder

import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.RecorderActivityExerciseBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.dataholder.Labels
import com.orcharddu.fitty.model.Exercise
import com.orcharddu.fitty.ui.BaseActivity
import com.orcharddu.fitty.ui.recorder.adapter.CheckableExercisePlanRecyclerViewAdapter

class RecorderExerciseActivity : BaseActivity() {

    private lateinit var binding: RecorderActivityExerciseBinding
    private var background: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecorderActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        val exercisePlans = Labels.exercisePlans.toList()

        val calorieIntake = DataHolder.instance().dailyEvents().calorieIntake()

        exercisePlans.forEach {
            if (calorieIntake < 100) {
                it.duration = (100 / it.calorieFactor).toInt()
            } else {
                it.duration = (calorieIntake / it.calorieFactor).toInt()
            }
        }

        binding.apply {

            DataHolder.instance().dataHolder["screenshot"]?.also {
                this@RecorderExerciseActivity.background = it as Bitmap
                binding.background.setImageBitmap(this@RecorderExerciseActivity.background)
            }

            btnClose.setOnClickListener {
                DataHolder.instance().dailyEvents().exercisePlans.addAll(0,
                    exercisePlans.asSequence().filter { x -> x.checked}.onEach { x -> x.checked = false }.toList())
                onBackPressed()
            }

            val adapter = CheckableExercisePlanRecyclerViewAdapter(exercisePlans)
            recyclerviewExercisePlan.apply {
                setLayoutManager(LinearLayoutManager(context))
                setNestedScrollingEnabled(false)
                setItemAnimator(DefaultItemAnimator())
                setAdapter(adapter)
            }
            adapter.setOnCheckedListener { _, _, _ ->
                if (exercisePlans.any{ x -> x.checked}) {
                    imgClose.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_yes, null))
                } else {
                    imgClose.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close, null))
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.popup_fadeout)
    }

    override fun onDestroy() {
        super.onDestroy()
        background?.recycle()
    }
}

