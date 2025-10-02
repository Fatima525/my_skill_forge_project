package com.example.fyp_skill_forge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fyp_skill_forge.databinding.FragmentSplashBinding
import com.example.fyp_skill_forge.student.Home
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            delay(3000)
            if (finishedOnboarding()) {
                if (isUserLoggedIn()) {
                    // Navigate to home activity
                    val intent = Intent(requireActivity(), Home::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    // Navigate to Welcome screen directly
                    findNavController().navigate(R.id.action_viewPagerFragment_to_loginSignUp)
                }
            } else {
                // Navigate to onboarding
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun finishedOnboarding(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    private fun isUserLoggedIn(): Boolean {
        val pref: SharedPreferences = requireActivity().getSharedPreferences("userLoginInfo", Context.MODE_PRIVATE)
        return pref.getBoolean("flag", false)
    }
}
