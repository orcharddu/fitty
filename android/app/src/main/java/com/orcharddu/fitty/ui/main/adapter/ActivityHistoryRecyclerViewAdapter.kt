package com.orcharddu.fitty.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.MainActivityHistoryItemBinding
import com.orcharddu.fitty.dataholder.Resources
import com.orcharddu.fitty.model.Activity
import com.orcharddu.fitty.utils.UIUtils

class ActivityHistoryRecyclerViewAdapter(private var items: List<Activity>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: ((View, Int) -> Unit)? = null
    var onItemLongClickListener: ((View, Int) -> Boolean)? = null



    class DefaultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val defaultIcon = ResourcesCompat.getDrawable(view.resources, R.drawable.ic_activity_sport, null)
        val binding = MainActivityHistoryItemBinding.bind(view)
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return DefaultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_activity_history_item, parent, false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DefaultViewHolder -> {
                holder.apply {
                    val item = items[position]
                    binding.imgActivity.setImageDrawable(
                        UIUtils.drawableWithColorFilter(binding.root.context,
                            Resources.instance().icons[item.icon]?.constantState?.newDrawable() ?: defaultIcon!!, R.color.white)
                    )
                    binding.textActivity.text = item.name
                    binding.textCalorie.text = if (item.type == Activity.Type.FOOD) "+${item.calorie}kCal" else "-${item.calorie}kCal"
                    binding.textTime.text = item.time

                    itemView.setOnClickListener {
                        onItemClickListener?.let { f -> f(holder.itemView, holder.layoutPosition) }
                    }
                    itemView.setOnLongClickListener {
                        onItemLongClickListener?.let { f -> f(holder.itemView, holder.layoutPosition)}?: true
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}