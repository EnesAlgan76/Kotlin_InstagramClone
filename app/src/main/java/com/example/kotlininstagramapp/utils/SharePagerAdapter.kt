package com.example.kotlininstagramapp.utils

import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kotlininstagramapp.ui.Share.ShareCameraFragment
import com.example.kotlininstagramapp.ui.Share.ShareGalleryFragment
import com.example.kotlininstagramapp.ui.Share.ShareVideoFragment

class SharePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ShareCameraFragment()
            1 -> ShareVideoFragment()
            2 -> ShareGalleryFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

}

