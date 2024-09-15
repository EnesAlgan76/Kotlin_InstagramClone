package com.example.kotlininstagramapp.utils

import androidx.navigation.NavController
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.ns.ui.NSBottomNavView
import com.example.ns.ui.MenuItem

class BottomNavHandler {
    companion object {
        fun setupBottomNavBar(bottomNavView: NSBottomNavView, navController: NavController){
            val menuItems = listOf(
                MenuItem(id = 0,  activeIconRes = R.drawable.home_active, inactiveIconRes =R.drawable.home ),
                MenuItem(id = 1,  activeIconRes = R.drawable.search_active, inactiveIconRes = R.drawable.search),
                MenuItem(id = 2,  activeIconRes = R.drawable.add_active, inactiveIconRes = R.drawable.add),
                MenuItem(id = 3,  activeIconRes = R.drawable.bell_active,inactiveIconRes = R.drawable.bell),
                MenuItem(id = 4,  activeIconRes = R.drawable.user_active,inactiveIconRes = R.drawable.user)
            )

            bottomNavView.setMenu(menuItems)
            bottomNavView.onItemSelectedListener = { menuItem ->
                // Handle menu item selection
                when (menuItem.id) {
                    0 -> {
                        if (navController.currentDestination?.id != R.id.homeFragment) {
                            navController.navigate(R.id.homeFragment)
                        }
                    }
                    1 -> {
                        if (navController.currentDestination?.id != R.id.searchFragment) {
                            navController.navigate(R.id.searchFragment)
                        }

                    }
                    2 ->{
                        if (navController.currentDestination?.id != R.id.shareFragment) {
                            navController.navigate(R.id.shareFragment)
                        }
                    }
                    3 ->{
                        if (navController.currentDestination?.id != R.id.notificationsFragment) {
                            navController.navigate(R.id.notificationsFragment)
                        }
                    }
                    4 -> {
                        if (navController.currentDestination?.id != R.id.profileFragment) {
                            navController.navigate(R.id.profileFragment)
                        }
                    }
                }
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.homeFragment -> bottomNavView.setSelectedItem(0)
                    R.id.searchFragment -> bottomNavView.setSelectedItem(1)
                    R.id.shareFragment -> bottomNavView.setSelectedItem(2)
                    R.id.notificationsFragment -> {
                        bottomNavView.setSelectedItem(3)
                        FirebaseHelper().markNotificationsAsRead()
                    }
                    R.id.profileFragment -> bottomNavView.setSelectedItem(4)
                }
            }


        }
    }
}