package com.example.kotlininstagramapp.Reels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlininstagramapp.Home.HomeFragment
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R

class ReelsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reels)
      //  HomeFragment().bottomNavigationView.menu.getItem(3).setChecked(true)
      //  BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView),3)
    }
}