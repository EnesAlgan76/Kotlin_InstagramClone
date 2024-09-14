package com.example.kotlininstagramapp.ui.Share

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlininstagramapp.databinding.FragmentShareBinding
import com.example.kotlininstagramapp.utils.SharePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ShareFragment : Fragment() {

    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    val PERMISSION_CAMERA_CODE = 0
    val PERMISSION_STORAGE_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("******* share fragment")
        controlPermissions()
        setupShareViewPager()
    }

    private fun controlPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), PERMISSION_CAMERA_CODE)
        } else {
            // All permissions are already granted.
            Toast.makeText(requireContext(), "TÜM İZİNLER ZATEN ALINDI", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("İZİN İÇİN BEKLENİYOR...")

        var permissionsDenied = false

        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                permissionsDenied = true
                break
            }
        }

        if (permissionsDenied) {
            requireActivity().finish()
            Toast.makeText(requireContext(), "İZİNLERDEN EN AZ BİRİ VERİLMEDİ", Toast.LENGTH_SHORT).show()
        } else {
            // All permissions are granted.
            Toast.makeText(requireContext(), "TÜM İZİNLER ALINDI", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupShareViewPager() {
        val myPagerAdapter = SharePagerAdapter(requireActivity())
        binding.viewPager.adapter = myPagerAdapter
        binding.viewPager.currentItem = 1
        binding.viewPager.offscreenPageLimit = 1

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "KAMERA"
                1 -> tab.text = "VİDEO"
                2 -> tab.text = "GALERİ"
                else -> tab.text = "Tab ${position + 1}"
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
