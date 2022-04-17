package com.orcharddu.fitty.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.orcharddu.fitty.ui.BaseActivity
import com.orcharddu.fitty.R

import com.orcharddu.fitty.databinding.MainActivityBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.ui.main.fragment.DiaryFragment
import com.orcharddu.fitty.ui.main.fragment.MeFragment
import com.orcharddu.fitty.ui.main.fragment.TodayFragment


class MainActivity : BaseActivity() {

    lateinit var binding: MainActivityBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        title = "fitty"

        binding.apply {
            viewpager.apply {
                adapter = object : FragmentStateAdapter(this@MainActivity) {
                    override fun getItemCount(): Int {
                        return 3
                    }
                    override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> TodayFragment.newInstance(getStatusBarHeight(), getAppBarHeight())
                            1 -> DiaryFragment.newInstance(getStatusBarHeight(), getAppBarHeight())
                            2 -> MeFragment.newInstance(getStatusBarHeight(), getAppBarHeight())
                            else -> MeFragment()
                        }
                    }
                }
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.bottomNavigation.menu.getItem(position).isChecked = true
                    }
                })
                isUserInputEnabled = false
                offscreenPageLimit = 3
            }

            bottomNavigation.apply {
                fun performClickNavigationItem(index: Int) {
                    if (binding.viewpager.currentItem != index) {
                        if (!binding.viewpager.isFakeDragging) {
                            binding.viewpager.setCurrentItem(index, true)
                        }
                    }
                }
                requestLayout()
                setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.main_nav_today -> performClickNavigationItem(0)
                        R.id.main_nav_diary -> performClickNavigationItem(1)
                        R.id.main_nav_me    -> performClickNavigationItem(2)
                    }
                    true
                }
                setOnClickListener { /*Do nothing*/ }

            }
        }
    }
    
}