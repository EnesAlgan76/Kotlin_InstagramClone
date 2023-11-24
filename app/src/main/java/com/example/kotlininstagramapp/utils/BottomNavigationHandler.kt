package com.example.kotlininstagramapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import com.example.kotlininstagramapp.Home.HomeActivity
import com.example.kotlininstagramapp.Login.RegisterActivity
import com.example.kotlininstagramapp.Profile.ProfileActivity
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.Reels.ReelsActivity
import com.example.kotlininstagramapp.Search.SearchActivity
import com.example.kotlininstagramapp.Share.ShareActivity
import com.google.android.material.navigation.NavigationBarView

class BottomNavigationHandler {
    companion object{
        var currentMenuNo = 0

        fun setupNavigations(context: Context, bottomNavigationView: NavigationBarView, menuNo: Int) {
            bottomNavigationView.menu.getItem(menuNo).setChecked(true)

            bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.menu_item_add -> {
                            currentMenuNo =0
                            val intent = Intent(context, ShareActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).overridePendingTransition(0, 0)
                            return true
                        }
                        R.id.menu_item_home -> {
                            Log.e("-----------",menuNo.toString())

                            if(currentMenuNo!=4){
                                val intent = Intent(context, HomeActivity::class.java)
                                context.startActivity(intent)
                                (context as Activity).overridePendingTransition(0, 0)
                            }
                            currentMenuNo =4

                            return true
                        }
                        R.id.menu_item_profile -> {
                            val intent = Intent(context, ProfileActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).overridePendingTransition(0, 0)
                            currentMenuNo =0
                            return true
                        }
                        R.id.menu_item_search -> {
                            val intent = Intent(context, SearchActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).overridePendingTransition(0, 0)
                            currentMenuNo =0
                            return true
                        }
                        R.id.menu_item_video -> {
                            val intent = Intent(context, ReelsActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).overridePendingTransition(0, 0)
                            currentMenuNo =0
                            return true
                        }
                        else -> {
                            return false
                        }
                    }
                }
            })
        }

    }
}