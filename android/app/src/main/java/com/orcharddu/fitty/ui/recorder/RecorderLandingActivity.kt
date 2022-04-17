package com.orcharddu.fitty.ui.recorder

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.RecorderActivityLandingBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.ui.BaseActivity
import com.orcharddu.fitty.ui.main.fragment.DiaryFragment
import com.orcharddu.fitty.ui.main.fragment.MeFragment
import com.orcharddu.fitty.ui.main.fragment.TodayFragment
import com.orcharddu.fitty.ui.recorder.fragment.DefaultFoodRecorderFragment
import com.orcharddu.fitty.ui.recorder.fragment.FavoriteFoodRecorderFragment
import com.orcharddu.fitty.utils.UIUtils

class RecorderLandingActivity : BaseActivity() {

    private lateinit var binding: RecorderActivityLandingBinding
    private var background: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecorderActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener {
                onBackPressed()
            }

            viewpager.apply {
                adapter = object : FragmentStateAdapter(this@RecorderLandingActivity) {
                    override fun getItemCount() = 2
                    override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> DefaultFoodRecorderFragment()
                            1 -> FavoriteFoodRecorderFragment()
                            else -> FavoriteFoodRecorderFragment()
                        }
                    }
                }
                offscreenPageLimit = 2
            }

            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                when (position) {
                    0 -> tab.text = "DEFAULT"
                    1 -> tab.text = "FAVORITES"
                }
            }.attach()

        }

        DataHolder.instance().dataHolder["screenshot"]?.also {
            background = it as Bitmap
            binding.background.setImageBitmap(background)
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.popup_fadeout)
    }

    override fun onDestroy() {
        super.onDestroy()
        background?.recycle()
    }
}