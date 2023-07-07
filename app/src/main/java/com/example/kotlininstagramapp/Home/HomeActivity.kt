package com.example.kotlininstagramapp.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityHomeBinding
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.utils.MyPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // binding.bottomNavigationView.setOnItemSelectedListener(this)
        setupHomeViewPager()
        BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView),4)
    }

    private fun setupHomeViewPager() {
        var myPagerAdapter = MyPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = myPagerAdapter
        binding.viewPager.currentItem = 1
    }

}


