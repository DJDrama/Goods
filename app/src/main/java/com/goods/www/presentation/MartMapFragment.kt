package com.goods.www.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.goods.www.R
import com.goods.www.databinding.FragmentMartMapBinding

class MartMapFragment : Fragment(R.layout.fragment_mart_map) {

    private var _binding: FragmentMartMapBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMartMapBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}