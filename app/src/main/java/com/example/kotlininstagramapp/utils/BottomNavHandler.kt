package com.example.kotlininstagramapp.utils

import androidx.navigation.NavController
import com.example.kotlininstagramapp.R
import com.example.ns.ui.NSBottomNavView
import com.example.ns.ui.MenuItem

class BottomNavHandler {
    companion object {
        fun setupBottomNavBar(bottomNavView: NSBottomNavView, navController: NavController){
            val menuItems = listOf(
                MenuItem(id = 1,  selectedIconRes = R.drawable.home, unselectedIconRes = R.drawable.icon_home),
                MenuItem(id = 2,  selectedIconRes = R.drawable.icon_search, unselectedIconRes = R.drawable.icon_search),
                MenuItem(id = 3,  selectedIconRes = R.drawable.user, unselectedIconRes = R.drawable.icon_profile)
            )

            bottomNavView.setMenu(menuItems)
            bottomNavView.onItemSelectedListener = { menuItem ->
                // Handle menu item selection
                when (menuItem.id) {
                    1 -> {
                        if (navController.currentDestination?.id != R.id.homeFragment) {
                            navController.navigate(R.id.homeFragment)
                        }
                    }
                    2 -> {
                        if (navController.currentDestination?.id != R.id.searchFragment) {
                            navController.navigate(R.id.searchFragment)
                        }
                    }
                    3 -> {
                        if (navController.currentDestination?.id != R.id.profileFragment) {
                            navController.navigate(R.id.profileFragment)
                        }
                    }
                }
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.homeFragment -> bottomNavView.setSelectedItem(1)
                    R.id.searchFragment -> bottomNavView.setSelectedItem(2)
                    R.id.shareFragment -> bottomNavView.setSelectedItem(3)
                   // R.id.profileFragment -> bottomNavView.selectedItemId = R.id.nav_profile
                   // else -> bottomNavView.selectedItemId = R.id.nav_home
                }
            }


        }
    }
}