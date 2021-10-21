package com.goods.www.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.goods.www.R
import com.goods.www.databinding.FragmentItemMapBinding

class ItemMapFragment : Fragment(R.layout.fragment_item_map) {

    private var _binding: FragmentItemMapBinding? = null
    private val binding get() = _binding!!

    private val shopDetailViewModel by activityViewModels<ShopDetailViewModel>()

    private lateinit var locationAdapter: LocationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemMapBinding.bind(view)
        initViews()
        subscribeToObservers()
    }

    private fun initViews() {
        locationAdapter = LocationAdapter()
        binding.recyclerView.apply {
            adapter = locationAdapter
        }
    }

    private fun subscribeToObservers() {
        shopDetailViewModel.categorySet.observe(viewLifecycleOwner) {
            it?.let {
                locationAdapter.submitList(it.toList())
            }
        }
        shopDetailViewModel.currentItem.observe(viewLifecycleOwner) {
            it?.let {
                Glide.with(binding.root.context).load(it.img).into(binding.ivItem)
                binding.tvItemName.text = it.name
                binding.tvPrice.text = "가격: ${it.price}원"
                binding.tvType.text = "분류: ${it.type}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}