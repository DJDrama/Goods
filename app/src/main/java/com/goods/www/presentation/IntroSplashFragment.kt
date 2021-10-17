package com.goods.www.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goods.www.R
import com.goods.www.databinding.FragmentIntroSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroSplashFragment : Fragment(R.layout.fragment_intro_splash) {
    private var _binding: FragmentIntroSplashBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentIntroSplashBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        lifecycleScope.launch {
            delay(1500)
            findNavController().navigate(R.id.action_introSplashFragment_to_shopListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}