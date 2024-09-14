package com.example.kotlininstagramapp.ui.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        fragmentNavigations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fragmentNavigations() {
        binding.tvEditprofile.setOnClickListener {
            findNavController().navigate(R.id.profileEditFragment)
        }

        binding.tvLogOut.setOnClickListener {
            LogOutFragment().show(parentFragmentManager, "LogoutDialog")
        }
    }

    private fun setupToolbar() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
