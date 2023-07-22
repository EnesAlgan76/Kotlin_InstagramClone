package com.example.kotlininstagramapp.utils

import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.Home.CameraFragment
import com.example.kotlininstagramapp.Home.HomeFragment
import com.example.kotlininstagramapp.Home.MessagesFragment

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kotlininstagramapp.Share.ShareCameraFragment
import com.example.kotlininstagramapp.Share.ShareGalleryFragment
import com.example.kotlininstagramapp.Share.ShareVideoFragment

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
