package com.example.kotlininstagramapp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kotlininstagramapp.Home.CameraFragment
import com.example.kotlininstagramapp.Home.HomeFragment
import com.example.kotlininstagramapp.Home.MessagesFragment

class MyPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        // Return the number of fragments
        return 3
    }

    override fun getItem(position: Int): Fragment {
        // Create and return the appropriate fragment based on the position
        return when (position) {
            0 -> CameraFragment()
            1 -> HomeFragment()
            2 -> MessagesFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }


}
