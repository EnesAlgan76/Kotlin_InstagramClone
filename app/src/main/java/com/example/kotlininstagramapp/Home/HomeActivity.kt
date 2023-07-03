package com.example.kotlininstagramapp.Home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.kotlininstagramapp.Profile.ProfileActivity
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.Reels.ReelsActivity
import com.example.kotlininstagramapp.Search.SearchActivity
import com.example.kotlininstagramapp.Share.ShareActivity
import com.example.kotlininstagramapp.databinding.ActivityHomeBinding
import com.example.kotlininstagramapp.utils.MyPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class HomeActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.setOnItemSelectedListener(this)


        setupHomeViewPager()
    }

    private fun setupHomeViewPager() {
        var myPagerAdapter = MyPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = myPagerAdapter
        binding.viewPager.currentItem = 1
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        print("clicked")
        when(item.itemId){
            R.id.menu_item_add ->{
                startActivity(Intent(this, ShareActivity::class.java))
                overridePendingTransition(0, 0)
                return true
            }

            R.id.menu_item_home ->{
                startActivity(Intent(this,HomeActivity::class.java))
                overridePendingTransition(0, 0)
                return true
            }

            R.id.menu_item_profile ->{
                startActivity(Intent(this, ProfileActivity::class.java))
                overridePendingTransition(0, 0)
                return true
            }

            R.id.menu_item_search ->{
                startActivity(Intent(this, SearchActivity::class.java))
                overridePendingTransition(0, 0)
                return true
            }

            R.id.menu_item_video ->{
                startActivity(Intent(this, ReelsActivity::class.java))
                overridePendingTransition(0, 0)
                return true
            }
        }

        return false
    }
}