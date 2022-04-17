package com.orcharddu.fitty.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.RecorderExercisePlanItemBinding
import com.orcharddu.fitty.dataholder.Resources
import com.orcharddu.fitty.model.Exercise


class ExercisePlanRecyclerViewAdapter(private var items: List<Exercise>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener: ((View, Int) -> Unit)? = null
    private var onItemLongClickListener: ((View, Int) -> Boolean)? = null
    fun setOnItemClickListener(listener: (View, Int) -> Unit) { onItemClickListener = listener }
    fun setOnItemLongClickListener(listener: (View, Int) -> Boolean) { onItemLongClickListener = listener }

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
                    isCheckable = false
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
                    setOnCheckedChangeListener { _, isChecked ->
                        item.checked = isChecked
                    }

                    setOnViewClickListener {
                        onItemClickListener?.let { f -> f(holder.itemView, position) }
                    }
                    setOnViewLongClickListener {
                        onItemLongClickListener?.let { f -> f(holder.itemView, position) } ?: true
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

}