package com.goods.www.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.goods.www.R
import com.goods.www.databinding.FragmentMartMapBinding

class MartMapFragment : Fragment(R.layout.fragment_mart_map) {
    private var _binding: FragmentMartMapBinding? = null
    private val binding get() = _binding!!

    private val args: MartMapFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMartMapBinding.bind(view)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.show()

        args.shopItem?.let{
            actionBar?.setTitle(it.name)
        } ?: actionBar?.setTitle("내 주변 마트")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}