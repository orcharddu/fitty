package com.orcharddu.fitty.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.MainFragmentMeBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.ui.main.MainActivity
import com.orcharddu.fitty.ui.recorder.RecorderExerciseActivity
import com.orcharddu.fitty.utils.Utils
import kotlin.math.pow

private const val ARG_PARAM1 = "statusBarHeight"
private const val ARG_PARAM2 = "appBarHeight"

class MeFragment : Fragment() {
    private var statusBarHeight: Int? = null
    private var appBarHeight: Int? = null

    private lateinit var binding: MainFragmentMeBinding

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
        binding = MainFragmentMeBinding.inflate(layoutInflater)
        initView()
        initChart()
        updateChart()
        return binding.root
    }


    private fun initView() {
        binding.apply {
            linearlayout.setPadding(0, statusBarHeight!!, 0, 0)
            initWeight()
            initGoal()

            btnDeleteAccount.setOnClickListener {
                DataHolder.instance().reset()
                DataHolder.instance().save(requireContext())
                startActivity(Intent().apply {
                    setClass(binding.root.context, MainActivity::class.java)
                })
                activity?.finish()
            }

            switchIntegratedCamera.isChecked = DataHolder.instance().user.useIntegratedCamera
            switchIntegratedCamera.setOnCheckedChangeListener { _, checked ->
                DataHolder.instance().user.useIntegratedCamera = checked
                DataHolder.instance().save(requireContext())
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initWeight() {
        binding.apply {
            layoutWeights.visibility = View.GONE
            btnCloseWeight.setOnClickListener {
                layoutWeights.visibility = View.GONE
                TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                btnRecordWeight.text = "RECORD NEW WEIGHTS"
            }

            sliderWeight.addOnChangeListener { _, value, _ ->
                btnWeight.setText("${value.toInt()}KG")
            }
            sliderWeight.setLabelFormatter { value: Float ->
                value.let { "${it.toInt()}KG" }
            }

            sliderHeight.addOnChangeListener { _, value, _ ->
                btnHeight.setText("${value.toInt()}CM")
            }
            sliderHeight.setLabelFormatter { value: Float ->
                value.let { "${it.toInt()}CM" }
            }

            btnRecordWeight.setOnClickListener {
                when (layoutWeights.visibility) {
                    View.VISIBLE -> {
                        textWeight.text = sliderWeight.value.toInt().toString()
                        textHeight.text = sliderHeight.value.toInt().toString()
                        textBmi.text = (sliderWeight.value / (sliderHeight.value.toDouble() / 100).pow(2.0)).let {
                            Utils.doubleToStringWithOneDecimal(it)
                        }
                        DataHolder.instance().user.weights.add(sliderWeight.value.toInt())
                        DataHolder.instance().user.heights.add(sliderHeight.value.toInt())
                        layoutWeights.visibility = View.GONE
                        TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                        btnRecordWeight.text = "RECORD NEW WEIGHTS"
                        updateChart()
                    }
                    else -> {
                        if (textWeight.text.isNotEmpty() && textHeight.text.isNotEmpty()) {
                            sliderWeight.value = textWeight.text.toString().toFloat()
                            sliderHeight.value = textHeight.text.toString().toFloat()
                        }
                        TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                        layoutWeights.visibility = View.VISIBLE
                        btnRecordWeight.text = "SAVE"
                    }
                }
            }

            textWeight.text = ""
            textHeight.text = ""
            textBmi.text = ""

            DataHolder.instance().user.weights.let {
                if (it.isNotEmpty()) {
                    textWeight.text = it.last().toString()
                    sliderWeight.value = it.last().toFloat()
                }
            }

            DataHolder.instance().user.heights.let {
                if (it.isNotEmpty()) {
                    textHeight.text = it.last().toString()
                    sliderHeight.value = it.last().toFloat()
                }
            }

            if (textWeight.text.isNotEmpty() && textHeight.text.isNotEmpty()) {
                textBmi.text =
                    (textWeight.text.toString().toDouble() / (textHeight.text.toString().toDouble() / 100).pow(2.0)).let {
                        Utils.doubleToStringWithOneDecimal(it)
                    }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun initGoal() {
        binding.apply {
            layoutGoal.visibility = View.GONE
            btnCloseGoal.setOnClickListener {
                layoutGoal.visibility = View.GONE
                TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                btnRecordGoal.text = "SET MY GOAL"
            }
            sliderGoal.addOnChangeListener { _, value, _ ->
                btnGoal.setText("${value.toInt()}kCal")
            }
            sliderGoal.setLabelFormatter { value: Float ->
                value.let { "${it.toInt()}kCal" }
            }

            btnRecordGoal.setOnClickListener {
                when (layoutGoal.visibility) {
                    View.VISIBLE -> {
                        DataHolder.instance().user.calorieGoal = sliderGoal.value.toInt()
                        layoutGoal.visibility = View.GONE
                        TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                        btnRecordGoal.text = "SET MY GOAL"
                        updateGoal()
                    }
                    else -> {
                        if (textGoalTotal.text.isNotEmpty()) {
                            sliderGoal.value = textGoalTotal.text.toString().toInt().let {
                                (it / 100 * 100).toFloat()
                            }
                        }
                        TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                        layoutGoal.visibility = View.VISIBLE
                        btnRecordGoal.text = "SET"
                    }
                }
            }
            updateGoal()
        }
    }


    private fun initChart() {
        binding.chartWeights.apply {
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
                return "${entry?.y?.toInt()}KG"
            }
        })
        Handler(Looper.getMainLooper()).postDelayed({
            binding.chartWeights.setData(lineData)
            binding.chartWeights.notifyDataSetChanged()
            binding.chartWeights.invalidate()
        }, 0)
    }

    private fun updateChart() {
        val dataset = DataHolder.instance().user.weights
        val slice = when {
            dataset.size > 5    -> dataset.slice((dataset.lastIndex - 4..dataset.lastIndex))
            else                -> dataset
        }

        val chartData = mutableListOf(
            if (slice.isEmpty()) Entry(0F, 0F)
            else Entry(0F, slice[0].toFloat())
        )

        for ((i, weight) in slice.withIndex()) {
            chartData.add(Entry((i + 1).toFloat(), weight.toFloat()))
        }

        chartData.add(
            if (slice.isEmpty()) Entry(chartData.size.toFloat(), 0F)
            else Entry(chartData.size.toFloat(), slice.last().toFloat())
        )
        setChartData(chartData)
        DataHolder.instance().save(requireContext())
    }

    private fun updateGoal() {
        binding.apply {
            textGoalCurrent.text = DataHolder.instance().dailyEvents().calorieIntake().toString()
            textGoalTotal.text = DataHolder.instance().user.calorieGoal.toString()
        }
        DataHolder.instance().save(requireContext())
    }

    override fun onResume() {
        super.onResume()
        updateGoal()
    }

    companion object {
        @JvmStatic
        fun newInstance(statusBarHeight: Int, appBarHeight: Int) =
            MeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, statusBarHeight)
                    putInt(ARG_PARAM2, appBarHeight)
                }
            }
    }
}
