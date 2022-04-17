package com.orcharddu.fitty.ui.recorder.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.MainFragmentTodayBinding
import com.orcharddu.fitty.databinding.RecorderFragmentDefaultFoodBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.ui.camera.CameraInferenceActivity
import com.orcharddu.fitty.ui.recorder.RecorderAddingActivity
import com.orcharddu.fitty.ui.recorder.RecorderLandingActivity
import com.orcharddu.fitty.utils.UIUtils

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DefaultFoodRecorderFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: RecorderFragmentDefaultFoodBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecorderFragmentDefaultFoodBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            icCamera.setImageDrawable(
                UIUtils.drawableWithColorFilter(
                    binding.root.context,
                    R.drawable.ic_camera,
                    R.color.white_text
                )
            )
            icBarcode.setImageDrawable(
                UIUtils.drawableWithColorFilter(
                    binding.root.context,
                    R.drawable.ic_bar_code,
                    R.color.white_text
                )
            )
            icAdd.setImageDrawable(
                UIUtils.drawableWithColorFilter(
                    binding.root.context,
                    R.drawable.ic_add,
                    R.color.white_text
                )
            )
            btnTakePhoto.setOnClickListener {
                startActivity(Intent().apply {
                    if (DataHolder.instance().user.useIntegratedCamera) {
                        putExtra("type", RecorderAddingActivity.Type.INTEGRATED_CAMERA)
                    } else {
                        putExtra("type", RecorderAddingActivity.Type.CAMERA)
                    }
                    setClass(it.context, RecorderAddingActivity::class.java)
                })
                activity?.finish()
            }

            btnAddManually.setOnClickListener {
                startActivity(Intent().apply {
                    putExtra("type", RecorderAddingActivity.Type.MANUAL)
                    setClass(it.context, RecorderAddingActivity::class.java)
                })
                activity?.finish()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DefaultFoodRecorderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}