package com.orcharddu.fitty.ui.recorder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.RecorderExercisePlanItemBinding
import com.orcharddu.fitty.dataholder.Resources
import com.orcharddu.fitty.model.Exercise


class CheckableExercisePlanRecyclerViewAdapter(private var items: List<Exercise>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener: ((View, Int) -> Unit)? = null
    private var onItemLongClickListener: ((View, Int) -> Boolean)? = null
    private var onCheckedListener: ((MaterialCardView, Boolean, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (View, Int) -> Unit) { onItemClickListener = listener }
    fun setOnItemLongClickListener(listener: (View, Int) -> Boolean) { onItemLongClickListener = listener }
    fun setOnCheckedListener(listener: (MaterialCardView, Boolean, Int) -> Unit) { onCheckedListener = listener }

    class DefaultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RecorderExercisePlanItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DefaultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recorder_exercise_plan_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DefaultViewHolder -> {
                val item = items[position]
                holder.binding.itemExercisePlan.apply {
                    isAdjustable = true
                    isCheckable = true
                    isChecked = item.checked
                    name = item.name
                    icon = Resources.instance().icons[item.icon]
                    duration = item.duration
                    calorie = item.calorie
                    calorieFactor = item.calorieFactor
                    item.duration = duration
                    item.calorie = calorie
                    setOnDurationAdjustedListener { duration ->
                        item.duration = duration
                        item.calorie = calorie
                    }
                    setOnCheckedChangeListener { card, isChecked ->
                        item.checked = isChecked
                        onCheckedListener?.invoke(card, isChecked, position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

}