package com.orcharddu.fitty.ui.recorder.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.RecorderFragmentFavoriteFoodItemBinding
import com.orcharddu.fitty.databinding.RecorderFragmentFavoriteFoodItemWithpicBinding
import com.orcharddu.fitty.model.Food
import com.orcharddu.fitty.utils.Utils

class FavoriteFoodRecyclerViewAdapter(private val foods: List<Food>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener: ((View, Int) -> Unit)? = null
    private var onItemLongClickListener: ((View, Int) -> Boolean)? = null
    fun setOnItemClickListener(listener: (View, Int) -> Unit) { onItemClickListener = listener }
    fun setOnItemLongClickListener(listener: (View, Int) -> Boolean) { onItemLongClickListener = listener }


    class DefaultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RecorderFragmentFavoriteFoodItemBinding.bind(view)
    }

    class PictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RecorderFragmentFavoriteFoodItemWithpicBinding.bind(view)
    }


    override fun getItemViewType(position: Int) = if (foods[position].picture == null) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> DefaultViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recorder_fragment_favorite_food_item, parent, false)
            )
            else -> PictureViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recorder_fragment_favorite_food_item_withpic, parent, false)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DefaultViewHolder -> {
                val food = foods[position]
                holder.binding.apply {
                    textFoodName.text = food.name
                    textFoodCalorie.text = "${food.calorie}kCal"
                    cardFavoriteFood.setOnClickListener {
                        onItemClickListener?.invoke(holder.itemView, position)
                    }
                    cardFavoriteFood.setOnLongClickListener {
                        onItemLongClickListener?.invoke(holder.itemView, position) ?: true
                    }
                }
            }
            is PictureViewHolder -> {
                val food = foods[position]
                holder.binding.apply {
                    textFoodName.text = food.name
                    textFoodCalorie.text = "${food.calorie}kCal"
                    cardFavoriteFood.setOnClickListener {
                        onItemClickListener?.invoke(holder.itemView, position)
                    }
                    cardFavoriteFood.setOnLongClickListener {
                        onItemLongClickListener?.invoke(holder.itemView, position) ?: true
                    }

                    food.picture?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size)
                    }?.let {
                        imgFood.setImageBitmap(Utils.bitmapScaled(it))
                    }
                }
            }
        }
    }

    override fun getItemCount() = foods.size
}