package com.orcharddu.fitty.ui.puzzle

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.PuzzleActivityWordLadderBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.ui.puzzle.adapter.WordsRecyclerViewAdapter
import com.orcharddu.fitty.utils.UIUtils
import studio.orchard.blurview.BlurView

class WordLadderActivity : AppCompatActivity() {
    private lateinit var binding: PuzzleActivityWordLadderBinding

    private val words = DataHolder.instance().dailyEvents().words
    private val answer = DataHolder.instance().dailyEvents().answer

    enum class Result {
        SUCCEED, NOT_A_WORD, NOT_ONE_LETTER_DIFFERENCE
    }

    companion object {
        private const val MAX_HINT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PuzzleActivityWordLadderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun initView() {
        binding.apply {
            btnClose.setOnClickListener {
                DataHolder.instance().save(applicationContext)
                finish()
            }

            recyclerview.apply {
                setLayoutManager(LinearLayoutManager(context))
                setNestedScrollingEnabled(false)
                setItemAnimator(DefaultItemAnimator())
                setAdapter(WordsRecyclerViewAdapter(words).also { adapter ->
                    adapter.progress = checkValidate(words).second
                    adapter.setOnEditorDoneListener { inputWord, position ->
                        words[position] = inputWord.text.toString()

                        checkValidate(words).let { (result, progress) ->
                            if (progress > position || progress > adapter.progress) {
                                adapter.progress = progress
                                adapter.notifyItemChanged(progress)
                                true
                            } else when (result) {
                                Result.NOT_A_WORD -> {
                                    Toast.makeText(applicationContext, "It is not a formal word!", Toast.LENGTH_LONG).show()
                                    false
                                }
                                Result.NOT_ONE_LETTER_DIFFERENCE -> {
                                    Toast.makeText(applicationContext, "The word should have only one different letter!", Toast.LENGTH_LONG).show()
                                    false
                                }
                                else -> false
                            }
                        }
                    }
                })
            }

            btnHint.setText("REMAINING HINTS: ${availableHints()}")
            btnHint.setOnClickListener {
                checkValidate(words).also { (result, progress) ->
                    when (result) {
                        Result.SUCCEED -> {
                            Toast.makeText(applicationContext, "You have completed the challenge!", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            if (DataHolder.instance().dailyEvents().hintCount >= MAX_HINT) {
                                Toast.makeText(applicationContext, "You have exhausted the number of hints!", Toast.LENGTH_LONG).show()
                                return@setOnClickListener
                            }
                            if (availableHints() == 0) {
                                Toast.makeText(applicationContext, "You do not have available hints!", Toast.LENGTH_LONG).show()
                                return@setOnClickListener
                            }
                            words[progress] = answer[progress]
                            DataHolder.instance().dailyEvents().hintCount++
                            (recyclerview.adapter as WordsRecyclerViewAdapter?)?.progress = progress + 1
                            recyclerview.adapter?.notifyDataSetChanged()
                            btnHint.setText("REMAINING HINTS: ${availableHints()}")
                        }
                    }
                }
            }

            btnComplete.setOnClickListener {
                checkValidate(words).also { (result, _) ->
                    when (result) {
                        Result.SUCCEED -> {
                            DataHolder.instance().dailyEvents().challenge = true
                            DataHolder.instance().save(applicationContext)
                            UIUtils.getBitmapFromView(this@WordLadderActivity).let { bitmap ->
                                BlurView.process(
                                    bitmap = bitmap,
                                    radius = 17f,
                                    scaling = 0.2f,
                                    mask = UIUtils.getColorDrawable("#30FFFFFF")
                                ).let { x ->
                                    DataHolder.instance().dataHolder["screenshot"] = x
                                }
                            }
                            startActivity(Intent().apply {
                                setClass(binding.root.context, CongratulationActivity::class.java)
                            })
                            overridePendingTransition(0, R.anim.popup_fadeout)
                            finish()
                        }
                        else -> {
                            Toast.makeText(applicationContext, "You have not met the conditions!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }
    }

    private fun availableHints(): Int {
        val historySize = DataHolder.instance().dailyEvents().activityHistory().size.let { x ->
            if (x > MAX_HINT) MAX_HINT else x
        }
        val hintCount = DataHolder.instance().dailyEvents().hintCount
        return (historySize - hintCount).let { x ->
            when {
                hintCount > MAX_HINT    -> 0
                x < 0                   -> 0
                else                    -> x
            }
        }
    }

    private fun checkValidate(words: List<String>): Pair<Result, Int> {
        var lastWord = words[0]
        for (i in (1..words.lastIndex)) {
            val word = words[i]
            if (!isWord(word)) {
                return Pair(Result.NOT_A_WORD, i)
            } else if (!isOneLetterDifferent(lastWord, word)) {
                return Pair(Result.NOT_ONE_LETTER_DIFFERENCE, i)
            } else {
                lastWord = word
            }
        }
        return Pair(Result.SUCCEED, words.size)
    }

    private fun isOneLetterDifferent(w1: String, w2: String): Boolean {
        if (w1.length != w2.length) return false
        var diff = 0
        for (i in w1.indices) {
            if (w1[i] != w2[i]) {
                diff++
            }
        }
        return (diff == 1)
    }

    private fun isWord(s: String): Boolean {
        return true
    }

}