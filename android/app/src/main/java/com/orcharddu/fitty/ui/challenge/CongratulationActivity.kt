package com.orcharddu.fitty.ui.challenge

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.ChallengeActivityCongratulationBinding
import com.orcharddu.fitty.dataholder.DataHolder

class CongratulationActivity : AppCompatActivity() {

    private lateinit var binding: ChallengeActivityCongratulationBinding
    private var background: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChallengeActivityCongratulationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        DataHolder.instance().dataHolder["screenshot"]?.also {
            background = it as Bitmap
            binding.background.setImageBitmap(background)
        }

        binding.apply {
            btnClose.setOnClickListener {
                overridePendingTransition(0, R.anim.popup_fadeout)
                finish()
            }
        }

    }
}