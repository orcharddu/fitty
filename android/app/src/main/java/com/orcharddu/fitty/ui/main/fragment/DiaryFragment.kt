package com.orcharddu.fitty.ui.main.fragment

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.MainFragmentDiaryBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.model.Activity
import com.orcharddu.fitty.ui.main.adapter.ActivityHistoryRecyclerViewAdapter
import com.orcharddu.fitty.utils.Utils
import java.util.*


private const val ARG_PARAM1 = "statusBarHeight"
private const val ARG_PARAM2 = "appBarHeight"

class DiaryFragment : Fragment() {

    private var statusBarHeight: Int? = null
    private var appBarHeight: Int? = null
    private lateinit var binding: MainFragmentDiaryBinding

    private val history = mutableListOf<Activity>()
    private var date = Utils.getCurrentDateInLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusBarHeight = it.getInt(ARG_PARAM1)
            appBarHeight = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentDiaryBinding.inflate(layoutInflater)
        initView()
        updateData()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            linearlayout.setPadding(0, statusBarHeight!!, 0, 0)
            calendarView.setMaxDate(calendarView.getDate())
            calendarView.setOnDateChangeListener { calendarView, yyyy, mm, dd ->
//                println("${Utils.getDateInLong(yyyy, mm, dd)}    ${Utils.getCurrentDateInLong()}")
                date = Utils.getDateInLong(yyyy, mm, dd)
                updateData()
            }

            recyclerviewHistory.apply {
                setLayoutManager(LinearLayoutManager(context))
                setNestedScrollingEnabled(false)
                setItemAnimator(DefaultItemAnimator())
                setAdapter(ActivityHistoryRecyclerViewAdapter(history).also {
                    it.onItemClickListener = { _: View, i: Int -> }
                    it.onItemLongClickListener = { _: View, _: Int -> true }
                })
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateData() {
        binding.apply {
            SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault()).also {
                textDate.text = it.format(Date(date)).uppercase(Locale.getDefault())
            }

            if (DataHolder.instance().dailyEvents(date).activityHistory().isNotEmpty()) {
                history.clear()
                history.addAll(DataHolder.instance().dailyEvents(date).activityHistory())
                binding.recyclerviewHistory.adapter?.notifyDataSetChanged()
                textGoalCurrent.text = DataHolder.instance().dailyEvents(date).calorieIntake().toString()
                imgAward.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        getResources(),
                        if (DataHolder.instance().dailyEvents(date).challenge) R.drawable.ic_awarded
                        else R.drawable.ic_award,
                        null
                    )
                )
                textNoHistory.visibility = View.INVISIBLE
                TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                layoutHistory.visibility = View.VISIBLE
            } else {
                layoutHistory.visibility = View.INVISIBLE
                TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                textNoHistory.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    companion object {
        @JvmStatic
        fun newInstance(statusBarHeight: Int, appBarHeight: Int) =
            DiaryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, statusBarHeight)
                    putInt(ARG_PARAM2, appBarHeight)
                }
            }
    }
}