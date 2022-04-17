package com.orcharddu.fitty.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.MainBottomSheetExercisePlanBinding
import com.orcharddu.fitty.dataholder.Resources
import com.orcharddu.fitty.model.Exercise
import com.orcharddu.fitty.utils.UIUtils

class ExercisePlanBottomSheetDialog : BottomSheetDialogFragment() {


    private var onRemoveButtonClick : (() -> Unit)? = null
    private var onCompleteButtonClick : (() -> Unit)? = null
    private var data: Exercise? = null

    fun setOnRemoveButtonClick(listener : () -> Unit) { onRemoveButtonClick = listener }
    fun setOnCompleteButtonClick(listener : () -> Unit) { onCompleteButtonClick = listener }

    fun bind(that: Exercise) {
        data = that
    }

    private lateinit var binding : MainBottomSheetExercisePlanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainBottomSheetExercisePlanBinding.inflate(layoutInflater)

        binding.apply {
            imgComplete.setImageDrawable(UIUtils.drawableWithColorFilter(requireContext(), R.drawable.ic_check, R.color.white))
            imgRemove.setImageDrawable(UIUtils.drawableWithColorFilter(requireContext(), R.drawable.ic_delete, R.color.white))
            btnClose.setOnClickListener {
                dismiss()
            }
            btnComplete.setOnClickListener {
                onCompleteButtonClick?.invoke()
                dismiss()
            }
            btnRemove.setOnClickListener {
                onRemoveButtonClick?.invoke()
                dismiss()
            }
            itemExercisePlan.apply {
                isAdjustable = false
                isCheckable = false
                data?.also {
                    name = it.name
                    icon = Resources.instance().icons[it.icon]
                    duration = it.duration
                    calorie = it.calorie
                    calorieFactor = it.calorieFactor
                }
            }

        }

        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}