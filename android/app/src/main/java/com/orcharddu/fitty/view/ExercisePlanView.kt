package com.orcharddu.fitty.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.card.MaterialCardView
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.ViewExercisePlanBinding

class ExercisePlanView : MaterialCardView, View.OnClickListener, View.OnLongClickListener {

    private lateinit var view: View
    private lateinit var binding: ViewExercisePlanBinding

    private var onViewClickListener : OnClickListener? = null
    private var onViewLongClickListener : OnLongClickListener? = null
    private var onDurationAdjustedListener : ((Int) -> Unit)? = null
    private val durationFormatter = { duration: Int -> if (duration < 10) 10 else if (duration > 240) 240 else duration / 10 * 10 }
    private val timeTextFormatter =  { duration: Int -> if (duration >= 60) "${duration / 60}h ${duration % 60}min" else "${duration}min" }
    private val calorieTextFormatter = { calorie: Int -> "${calorie}kCal" }

    var isAdjustable = true
        set(value) {
            field = value
            binding.btnAdjust.setVisibility(if (field) View.VISIBLE else View.GONE)
        }

    var name: String = ""
        set(value) {
            field = value
            binding.textExerciseName.setText(field)
        }

    var icon: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_exercise_jogging, null)
        set(value) {
            field = value
            binding.imgExercise.setImageDrawable(field)
        }

    var duration = 10 // in minute
        set(value) {
            field = durationFormatter(value)
            calorie = (field * calorieFactor).toInt()
            binding.textDuration.setText(timeTextFormatter(field))
            binding.sliderDuration.setValue(field.toFloat())
        }

    var calorie = 1000
        set(value) {
            field = value
            binding.textCalorie.setText(calorieTextFormatter((duration * calorieFactor).toInt()))
        }

    var calorieFactor = 1.0 // how much calorie consumed per duration
        set(value) {
            field = value
            calorie = (duration * field).toInt()
        }

    constructor(context: Context?) : super(context!!) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) { initView() }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        view = LayoutInflater.from(context).inflate(R.layout.view_exercise_plan, this)
        binding = ViewExercisePlanBinding.bind(view)
        binding.apply {
            btnAdjust.setOnClickListener {
                when (sliderDuration.visibility) {
                    View.VISIBLE -> {
                        sliderDuration.setVisibility(View.GONE)
                        TransitionManager.beginDelayedTransition(this@ExercisePlanView, AutoTransition())
                        btnAdjust.setText("ADJUST DURATION")
                    }
                    else -> {
                        TransitionManager.beginDelayedTransition(this@ExercisePlanView, AutoTransition())
                        sliderDuration.setVisibility(View.VISIBLE)
                        btnAdjust.setText("OK")
                    }
                }
            }

            sliderDuration.setLabelFormatter { value: Float ->
                duration = value.toInt()
                onDurationAdjusted(duration)
                timeTextFormatter(duration)
            }
            binding.textDuration.setText(timeTextFormatter(duration))
        }
        setOnClickListener(this)
        setOnLongClickListener(this)
    }

    override fun onClick(v: View?) {
        this.setChecked(!this.isChecked)
        onViewClickListener?.onClick(v)
    }

    override fun onLongClick(v: View?): Boolean {
        this.setChecked(!this.isChecked)
        onViewLongClickListener?.onLongClick(v)
        return true
    }

    private fun onDurationAdjusted(duration: Int) {
        onDurationAdjustedListener?.let { f -> f(duration) }
    }

    fun setOnViewClickListener(listener: OnClickListener) {
        onViewClickListener = listener
    }

    fun setOnViewLongClickListener(listener: OnLongClickListener) {
        onViewLongClickListener = listener
    }

    fun setOnDurationAdjustedListener(listener: (Int) -> Unit) {
        onDurationAdjustedListener = listener
    }

}