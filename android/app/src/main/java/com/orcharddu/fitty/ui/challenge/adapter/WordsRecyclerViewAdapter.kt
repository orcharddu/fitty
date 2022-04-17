package com.orcharddu.fitty.ui.challenge.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.ChallengeActivityWordLadderHeaderItemBinding
import com.orcharddu.fitty.databinding.ChallengeActivityWordLadderItemBinding

class WordsRecyclerViewAdapter(private val words: MutableList<String>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var progress = 1

    private var onEditorDoneListener: ((TextInputEditText, Int) -> Boolean)? = null

    fun setOnEditorDoneListener(listener: ((TextInputEditText, Int) -> Boolean)) { onEditorDoneListener = listener }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ChallengeActivityWordLadderHeaderItemBinding.bind(view)
    }

    class DefaultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ChallengeActivityWordLadderItemBinding.bind(view)
    }

    override fun getItemViewType(position: Int) = if (position == 0 || position == words.lastIndex) 0 else 1


    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> HeaderViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.challenge_activity_word_ladder_header_item, parent, false))
            else -> DefaultViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.challenge_activity_word_ladder_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.binding.apply {
                    imgRightArrow.visibility = View.INVISIBLE
                    textWord.text = words[position]
                }
            }
            is DefaultViewHolder -> {
                holder.binding.apply {
                    inputWord.setText(words[position])
                    inputWord.isEnabled = position <= progress
                    imgRightArrow.visibility = if (progress == position) View.VISIBLE else View.INVISIBLE
                    inputWord.setOnFocusChangeListener { _, focus ->
                        if (!focus) {
                            words[position] = inputWord.text.toString()
                        }
                    }
                    inputWord.setOnEditorActionListener { _, i, _ ->
                        if ((i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO) && inputWord.text.toString().isNotBlank()) {
                            onEditorDoneListener?.invoke(inputWord, position)?.let { succeed ->
                                inputLayout.boxStrokeColor = Color.parseColor(if (succeed) "#CC141414" else "#E37E7E")
                                imgRightArrow.visibility = if (progress == position) View.VISIBLE else View.INVISIBLE
                            }
                        }
                        false
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = words.size
}