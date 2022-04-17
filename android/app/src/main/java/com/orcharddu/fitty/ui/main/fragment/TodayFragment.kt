package com.orcharddu.fitty.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.MainFragmentTodayBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.dataholder.Resources
import com.orcharddu.fitty.model.Activity
import com.orcharddu.fitty.model.Exercise
import com.orcharddu.fitty.ui.challenge.WordLadderActivity
import com.orcharddu.fitty.ui.main.adapter.ActivityHistoryRecyclerViewAdapter
import com.orcharddu.fitty.ui.main.adapter.ExercisePlanRecyclerViewAdapter
import com.orcharddu.fitty.ui.main.bottomsheet.ExercisePlanBottomSheetDialog
import com.orcharddu.fitty.ui.recorder.RecorderExerciseActivity
import com.orcharddu.fitty.ui.recorder.RecorderLandingActivity
import com.orcharddu.fitty.utils.UIUtils
import com.orcharddu.fitty.utils.Utils

import studio.orchard.blurview.BlurView
import java.util.*


private const val ARG_PARAM1 = "statusBarHeight"
private const val ARG_PARAM2 = "appBarHeight"

@SuppressLint("NotifyDataSetChanged")
class TodayFragment : Fragment() {

    private var statusBarHeight: Int? = null
    private var appBarHeight: Int? = null
    private lateinit var binding: MainFragmentTodayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusBarHeight = it.getInt(ARG_PARAM1)
            appBarHeight = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentTodayBinding.inflate(layoutInflater)
        initView()
        initChart()
        initExercisePlans()
        initActivityHistory()
        updateData()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            linearlayout.setPadding(0, statusBarHeight!!, 0, 0)
            SimpleDateFormat("EE dd MMMM", Locale.getDefault()).also {
                textDate.text = it.format(Date(Utils.getCurrentDateInLong())).uppercase(Locale.getDefault())
            }

            btnRecordFood.setOnClickListener {
                activity?.let { activity ->
                    UIUtils.getBitmapFromView(activity).let { bitmap ->
                        BlurView.process(
                            bitmap = bitmap,
                            radius = 17f,
                            scaling = 0.2f,
                            mask = UIUtils.getColorDrawable("#30FFFFFF")
                        ).let { x ->
                            DataHolder.instance().dataHolder["screenshot"] = x
                        }
                    }
                }
                startActivity(Intent().apply {
                    setClass(binding.root.context, RecorderLandingActivity::class.java)
                })
                activity?.overridePendingTransition(0, R.anim.popup_fadeout)
            }
            btnRecordFood.setOnLongClickListener { true }

            btnRecordExercise.setOnClickListener {
                activity?.let { activity ->
                    UIUtils.getBitmapFromView(activity).let { bitmap ->
                        BlurView.process(
                            bitmap = bitmap,
                            radius = 17f,
                            scaling = 0.2f,
                            mask = UIUtils.getColorDrawable("#30FFFFFF")
                        ).let { x ->
                            DataHolder.instance().dataHolder["screenshot"] = x
                        }
                    }
                }
                startActivity(Intent().apply {
                    setClass(binding.root.context, RecorderExerciseActivity::class.java)
                })
                activity?.overridePendingTransition(0, R.anim.popup_fadeout)
            }

            imgChallenge.setImageDrawable(
                ResourcesCompat.getDrawable(
                    getResources(),
                    if (DataHolder.instance().dailyEvents().challenge) R.drawable.ic_awarded
                    else R.drawable.ic_award,
                    null
                )
            )

            btnChallenge.setOnClickListener {
                if (DataHolder.instance().dailyEvents().challenge) {
                    Toast.makeText(requireContext(), "You have completed today's challenge!", Toast.LENGTH_LONG).show()
                } else {
                    startActivity(Intent().apply {
                        setClass(binding.root.context, WordLadderActivity::class.java)
                    })
                }
            }

        }
    }

    private fun initExercisePlans() {
//        DataHolder.instance().dailyEvents().exercisePlans.addAll(
//            sequenceOf(
//                Exercise(
//                    calorieFactor = 15,
//                    duration = 40,
//                    name = "JOGGING",
//                    icon = Resources.Icon.JOGGING,
//                    checked = false),
//                Exercise(
//                    calorieFactor = 10,
//                    duration = 77,
//                    name = "RIDING",
//                    icon = Resources.Icon.RIDING,
//                    checked = false)
//            )
//        )

        val dialog = ExercisePlanBottomSheetDialog()

        binding.apply {

            val adapter = ExercisePlanRecyclerViewAdapter(DataHolder.instance().dailyEvents().exercisePlans)

            recyclerviewExercisePlan.apply {
                setLayoutManager(LinearLayoutManager(context))
                setNestedScrollingEnabled(false)
                setItemAnimator(DefaultItemAnimator())
                setAdapter(adapter)
            }


            val onClick = { _: View, position: Int ->
                dialog.bind(DataHolder.instance().dailyEvents().exercisePlans[position])
                dialog.setOnCompleteButtonClick {
                    DataHolder.instance().dailyEvents().exercisePlans[position].apply {
                        time = timeFormatter.format(now)
                        date = dateFormatter.format(now)
                        DataHolder.instance().dailyEvents().addActivityHistory(this)
                        binding.recyclerviewHistory.adapter?.notifyItemInserted(0)
                        DataHolder.instance().dailyEvents().exercisePlans.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyItemRangeChanged(position, DataHolder.instance().dailyEvents().exercisePlans.size)
                        updateData()
                    }
                }
                dialog.setOnRemoveButtonClick {
                    DataHolder.instance().dailyEvents().exercisePlans.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, DataHolder.instance().dailyEvents().exercisePlans.size)
                    updateData()
                }
                dialog.show(parentFragmentManager, ExercisePlanBottomSheetDialog.TAG)
            }
            val onLongClick = { view: View, position: Int ->
                onClick.invoke(view, position)
                true
            }

            adapter.setOnItemClickListener(onClick)
            adapter.setOnItemLongClickListener(onLongClick)

        }
    }

    private fun initActivityHistory() {
        binding.recyclerviewHistory.apply {
            setLayoutManager(LinearLayoutManager(context))
            setNestedScrollingEnabled(false)
            setItemAnimator(DefaultItemAnimator())
            setAdapter(ActivityHistoryRecyclerViewAdapter(DataHolder.instance().dailyEvents().activityHistory()).also {
                it.onItemClickListener = { _: View, i: Int -> }
                it.onItemLongClickListener = { _: View, _: Int -> true }
            })
        }
    }

    private fun initChart() {
        binding.chart.apply {
            description = Description().apply {
                text = ""
                textAlign = Paint.Align.CENTER
                textSize = 16F
            }
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
            isDragEnabled = false
            setDrawGridBackground(false)
            setTouchEnabled(false)
            setScaleEnabled(false)
            setBackgroundColor(Color.TRANSPARENT)
            setViewPortOffsets(0f, 30f, 0f, 0f)

            xAxis.apply {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setPosition(XAxis.XAxisPosition.BOTTOM)
                isEnabled = false
            }

            axisLeft.apply {
                removeAllLimitLines()
                isEnabled = false
            }

            axisRight.apply {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                removeAllLimitLines()
                isEnabled = false
            }
        }
        updateChart()
    }

    private fun setChartData(data: List<Entry>) {
        val set = LineDataSet(data, "").apply{
            setMode(LineDataSet.Mode.HORIZONTAL_BEZIER)
            setColor(ContextCompat.getColor(requireContext(), R.color.white))
            setLineWidth(3f)
            setAxisDependency(YAxis.AxisDependency.LEFT)
            setDrawFilled(true)
            setFillColor(ContextCompat.getColor(requireContext(), R.color.white))
            setDrawValues(true)
            setValueTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            setValueTextSize(13f)
            setCircleRadius(6f)
            setFormSize(0f)
            setForm(Legend.LegendForm.CIRCLE)
            setDrawCircleHole(false)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.white))

        }
        val lineDataSetList = listOf(set)
        val lineData = LineData(lineDataSetList)
        lineData.setValueFormatter(object : ValueFormatter() {
            override fun getPointLabel(entry: Entry?): String {
                return "${entry?.y?.toInt()}kCal"
            }
        })
        Handler(Looper.getMainLooper()).postDelayed({
            binding.chart.setData(lineData)
            binding.chart.notifyDataSetChanged()
            binding.chart.invalidate()
        }, 0)
    }

    private fun updateChart() {
        val dataset = DataHolder.instance().dailyEvents().activityHistory().asReversed()
//        val chartData = mutableListOf(Entry(0F, 0F))
        val chartData = mutableListOf(
            when {
                dataset.isEmpty() ->
                    Entry(0F, 0F)
                dataset[0].type == Activity.Type.FOOD ->
                    Entry(0F, dataset[0].calorie.toFloat())
                else ->
                    Entry(0F, -dataset[0].calorie.toFloat())
            }
        )

        var accumulator = 0F
        for ((i, history) in dataset.withIndex()) {
            chartData.add(when (history.type) {
                Activity.Type.FOOD -> {
                    accumulator += history.calorie
                    Entry((i + 1).toFloat(), accumulator)
                }
                Activity.Type.EXERCISE -> {
                    accumulator -= history.calorie
                    Entry((i + 1).toFloat(), accumulator)
                }
            })
        }
//        chartData.add(Entry(chartData.size.toFloat(), 0F))
        chartData.add(
            when {
                dataset.isEmpty() ->
                    Entry(chartData.size.toFloat(), 0F)
                else ->
                    Entry(chartData.size.toFloat(), accumulator)
            }
        )

        setChartData(chartData)
    }

    private fun updateGoal() {
        binding.apply {
            textGoalCurrent.text = DataHolder.instance().dailyEvents().calorieIntake().toString()
            textGoalTotal.text = DataHolder.instance().user.calorieGoal.toString()
        }
    }


    private fun updateHint() {
        binding.apply {
            textNoExercisePlan.visibility = if (DataHolder.instance().dailyEvents().exercisePlans.isEmpty()) View.VISIBLE else View.GONE
            textNoActivityHistory.visibility = if (DataHolder.instance().dailyEvents().activityHistory().isEmpty()) View.VISIBLE else View.GONE
            imgChallenge.setImageDrawable(
                ResourcesCompat.getDrawable(
                    getResources(),
                    if (DataHolder.instance().dailyEvents().challenge) R.drawable.ic_awarded
                    else R.drawable.ic_award,
                    null
                )
            )
        }
    }

    private fun updateData() {
        updateChart()
        updateGoal()
        updateHint()
        DataHolder.instance().save(requireContext())
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            recyclerviewExercisePlan.adapter?.notifyDataSetChanged()
            recyclerviewHistory.adapter?.notifyDataSetChanged()
            updateData()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(statusBarHeight: Int, appBarHeight: Int) =
            TodayFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, statusBarHeight)
                    putInt(ARG_PARAM2, appBarHeight)
                }
            }
    }

}