package com.goods.www.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.goods.www.R
import com.goods.www.databinding.FragmentShopDetailBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ShopDetailFragment : Fragment(R.layout.fragment_shop_detail) {

    private var _binding: FragmentShopDetailBinding? = null
    private val binding
        get() = _binding!!

    private val navArgs by navArgs<ShopDetailFragmentArgs>()
    private val shopDetailViewModel by activityViewModels<ShopDetailViewModel>()

    private lateinit var itemListAdapter: ItemListAdapter

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentShopDetailBinding.bind(view)
        shopDetailViewModel.getItems(navArgs.documentId ?: "")

        initViews()
        subscribeToObservers()
    }

    private fun initViews() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = navArgs.title
        itemListAdapter = ItemListAdapter {
            shopDetailViewModel.setCurrentItem(it)
            findNavController().navigate(R.id.action_shopListFragment_to_martMapFragment)
        }
        binding.recyclerView.apply {
            itemAnimator = null
            adapter = itemListAdapter
            addItemDecoration(DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL))
        }

        binding.edtSearch.addTextChangedListener {
            shopDetailViewModel.search(it.toString())
        }
    }

    private fun subscribeToObservers() {
        shopDetailViewModel.items.observe(viewLifecycleOwner) {
            it?.let {
                if (::itemListAdapter.isInitialized)
                    itemListAdapter.submitList(null)
                itemListAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}