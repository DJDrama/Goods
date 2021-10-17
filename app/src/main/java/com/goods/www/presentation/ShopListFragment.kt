package com.goods.www.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goods.www.R
import com.goods.www.databinding.FragmentShopListBinding

class ShopListFragment : Fragment(R.layout.fragment_shop_list) {

    private var _binding: FragmentShopListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShopListViewModel by viewModels()
    private lateinit var shopListAdapter: ShopListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentShopListBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        initViews()

        subscribeToObservers()
    }

    private fun initViews() {
        binding.tvNearbyMart.setOnClickListener {
            findNavController().navigate(R.id.action_shopListFragment_to_martMapFragment)
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        shopListAdapter = ShopListAdapter {
            val action = ShopListFragmentDirections.actionShopListFragmentToMartMapFragment(
                brandItem = it
            )
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            adapter = shopListAdapter
        }
    }

    private fun subscribeToObservers() {
        viewModel.shops.observe(viewLifecycleOwner) {
            it?.let {
                shopListAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}