package com.example.kotlininstagramapp.utils

import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.kotlininstagramapp.MainActivity
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.ns.ui.NSBottomNavView
import com.example.ns.ui.MenuItem
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.core.animation.addListener

class BottomNavHandler {
    companion object {
        fun setupBottomNavBar(
            activity: FragmentActivity,
            bottomNavView: NSBottomNavView,
            navController: NavController
        ){
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
                        initializeShareMenu(activity, navController)
                        bottomNavView.setSelectedItem(2)
                        //if (navController.currentDestination?.id != R.id.shareFragment) {
                        //    navController.navigate(R.id.shareFragment)
                        //}
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
                   // R.id.shareFragment -> bottomNavView.setSelectedItem(2)
                    R.id.notificationsFragment -> {
                        bottomNavView.setSelectedItem(3)
                        FirebaseHelper().markNotificationsAsRead()
                    }
                    R.id.profileFragment -> bottomNavView.setSelectedItem(4)
                }
            }


        }

        private fun initializeShareMenu(activity: FragmentActivity, navController: NavController) {
            val cvmanu = (activity as MainActivity).findViewById<CardView>(R.id.cv_shareMenu)
            val iv_camera = activity.findViewById<ImageView>(R.id.iv_camera)
            val iv_video = activity.findViewById<ImageView>(R.id.iv_video)
            val iv_gallery = activity.findViewById<ImageView>(R.id.iv_gallery)
            if (cvmanu.isVisible) {
                ObjectAnimator.ofFloat(cvmanu, "alpha", 1f, 0f).apply {
                    duration = 300 // duration of animation in milliseconds
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }.addListener(onEnd = {
                    cvmanu.visibility = View.GONE // Make it gone after animation ends
                })
            } else {
                cvmanu.visibility = View.VISIBLE
                cvmanu.alpha = 0f
                ObjectAnimator.ofFloat(cvmanu, "alpha", 0f, 1f).apply {
                    duration = 300 // duration of animation in milliseconds
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }

            iv_camera.setOnClickListener {
                navController.navigate(R.id.shareCameraFragment)
                cvmanu.visibility = View.GONE
            }

            iv_video.setOnClickListener {
                 navController.navigate(R.id.videoFragment)
                cvmanu.visibility = View.GONE
            }

            iv_gallery.setOnClickListener {
                navController.navigate(R.id.shareGalleryFragment)
                cvmanu.visibility = View.GONE
            }
        }
    }
}