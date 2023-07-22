package com.example.kotlininstagramapp.Share

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityShareBinding
import com.example.kotlininstagramapp.utils.MyPagerAdapter
import com.example.kotlininstagramapp.utils.SharePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ShareActivity : AppCompatActivity() {
    lateinit var binding: ActivityShareBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView),2)
        println("******* share activity")
        setupShareViewPager()
    }

    private fun setupShareViewPager() {
        var myPagerAdapter = SharePagerAdapter(this)
        binding.viewPager.adapter = myPagerAdapter
        binding.viewPager.currentItem = 1

        TabLayoutMediator(binding.tabLayout,binding.viewPager){ tab, position->
            when(position){
                0 -> tab.text = "KAMERA"
                1 -> tab.text = "VİDEO"
                2 -> tab.text = "GALERİ"
                else -> tab.text = "Tab ${position + 1}"
            }
        }.attach()
    }
}