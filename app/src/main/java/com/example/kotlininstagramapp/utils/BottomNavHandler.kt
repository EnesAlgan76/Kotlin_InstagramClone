package com.example.kotlininstagramapp.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.kotlininstagramapp.Home.HomeFragment
import com.example.kotlininstagramapp.MainActivity
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.ui.Profile.ProfileFragment
import com.example.kotlininstagramapp.ui.Search.SearchFragment
import com.example.kotlininstagramapp.ui.Share.ShareFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavHandler {
    companion object{
        fun setupBottomNavBar(bottomNavView: BottomNavigationView, activity: FragmentActivity, navController: NavController){
            bottomNavView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_item_home -> {
                         if (navController.currentDestination?.id != R.id.homeFragment) {
                             navController.navigate(R.id.homeFragment)
                         }
                        true
                    }
                    R.id.menu_item_search -> {
                        if (navController.currentDestination?.id != R.id.searchFragment) {
                            navController.navigate(R.id.searchFragment)
                        }
                        true
                    }
                    R.id.menu_item_add -> {
                        //if(currentFragment !is ShareFragment)
                         //   openFragment(ShareFragment())
                        // if (navController.currentDestination?.id != R.id.shareFragment) {
                        //     navController.navigate(R.id.shareFragment)
                        // }
                        true
                    }
                    R.id.menu_item_profile -> {
                        //if(currentFragment !is ProfileFragment)
                        //    openFragment(ProfileFragment())
                        if (navController.currentDestination?.id != R.id.profileFragment) {
                            navController.navigate(R.id.profileFragment)
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
}