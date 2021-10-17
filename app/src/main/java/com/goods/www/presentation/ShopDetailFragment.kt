package com.goods.www.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.goods.www.R
import com.goods.www.databinding.FragmentShopDetailBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ShopDetailFragment : Fragment(R.layout.fragment_shop_detail) {

    private var _binding: FragmentShopDetailBinding? = null
    private val binding
        get() = _binding!!

    private val navArgs by navArgs<ShopDetailFragmentArgs>()
    private val shopDetailViewModel by viewModels<ShopDetailViewModel>()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentShopDetailBinding.bind(view)
        shopDetailViewModel.getCategories(navArgs.documentId ?: "")
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        shopDetailViewModel.items.observe(viewLifecycleOwner){
            it?.let{

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}