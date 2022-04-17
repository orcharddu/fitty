package com.orcharddu.fitty.ui.recorder.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.orcharddu.fitty.R
import com.orcharddu.fitty.databinding.RecorderFragmentDefaultFoodBinding
import com.orcharddu.fitty.databinding.RecorderFragmentFavoriteFoodBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.ui.recorder.RecorderAddingActivity
import com.orcharddu.fitty.ui.recorder.adapter.FavoriteFoodRecyclerViewAdapter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavoriteFoodRecorderFragment : Fragment() {

    private lateinit var binding: RecorderFragmentFavoriteFoodBinding
    private var param1: String? = null
    private var param2: String? = null

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
        binding = RecorderFragmentFavoriteFoodBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            textNoFavorites.visibility = if (DataHolder.instance().user.favorites.isEmpty()) View.VISIBLE else View.INVISIBLE

            val adapter = FavoriteFoodRecyclerViewAdapter(DataHolder.instance().user.favorites)
            recyclerviewFavorites.apply {
                setLayoutManager(LinearLayoutManager(context))
                setNestedScrollingEnabled(false)
                setItemAnimator(DefaultItemAnimator())
                setAdapter(adapter)
            }
            adapter.setOnItemClickListener { _, i ->
                startActivity(Intent().apply {
                    putExtra("type", RecorderAddingActivity.Type.FAVORITES)
                    putExtra("favoriteIndex", i)
                    setClass(requireContext(), RecorderAddingActivity::class.java)
                })
                activity?.finish()
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFoodRecorderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}