package com.goods.www.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.goods.www.R
import com.goods.www.databinding.FragmentItemMapBinding

class ItemMapFragment : Fragment(R.layout.fragment_item_map) {

    private var _binding: FragmentItemMapBinding? = null
    private val binding get() = _binding!!

    private val navArgs by navArgs<ItemMapFragmentArgs>()

    private lateinit var locationAdapter: LocationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemMapBinding.bind(view)
        initViews()
    }

    private fun initViews() {
        locationAdapter = LocationAdapter()
        navArgs.item.apply {
            Glide.with(binding.root.context).load(img).into(binding.ivItem)
            binding.tvItemName.text = name
            binding.tvPrice.text = "가격: ${price}원"
            binding.tvType.text = "분류: $type"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}