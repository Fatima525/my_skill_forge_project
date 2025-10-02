package com.example.fyp_skill_forge.onboarding_Screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.navigation.fragment.findNavController
import com.example.fyp_skill_forge.R
import com.example.fyp_skill_forge.databinding.FragmentThirdOnboardingBinding

class ThirdOnboardingFragment : Fragment() {

    private var _binding: FragmentThirdOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.letsGo.setOnClickListener {
            finishedOnboarding()
            // ✅ Navigate using viewPagerFragment’s action, not splash
            findNavController().navigate(R.id.action_viewPagerFragment_to_loginSignUp)
        }
    }

    private fun finishedOnboarding() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("Finished", true)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
