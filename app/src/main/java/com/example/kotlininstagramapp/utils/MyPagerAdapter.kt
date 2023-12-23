package com.example.kotlininstagramapp.utils

import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.Home.CameraFragment
import com.example.kotlininstagramapp.Home.HomeFragment
import com.example.kotlininstagramapp.Home.ConversationsFragment

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CameraFragment()
            1 -> HomeFragment()
            2 -> ConversationsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}

