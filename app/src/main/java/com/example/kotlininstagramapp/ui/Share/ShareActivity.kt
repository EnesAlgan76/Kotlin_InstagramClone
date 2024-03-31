package com.example.kotlininstagramapp.ui.Share

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlininstagramapp.databinding.ActivityShareBinding
import com.example.kotlininstagramapp.utils.SharePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ShareActivity : AppCompatActivity() {
    lateinit var binding: ActivityShareBinding
    val PERMISSION_CAMERA_CODE = 0
    val PERMISSION_STORAGE_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView),2)
        println("******* share activity")
        controllPermissions()
        setupShareViewPager()

    }

    private fun controllPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissions.isNotEmpty()){
            ActivityCompat.requestPermissions(this,permissions.toTypedArray(),PERMISSION_CAMERA_CODE)
        } else {
            // All permissions are already granted.
            Toast.makeText(this, "TÜM İZİNLER ZATEN ALINDI", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("İZİN İÇİN BEKLENİYOR...")

        var permissionsDenied = false

        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                permissionsDenied = true
                break
            }
        }

        if (permissionsDenied) {
            finish()
            Toast.makeText(this, "İZİNLERDEN EN AZ BİRİ VERİLMEDİ", Toast.LENGTH_SHORT).show()
        } else {
            // All permissions are granted.
            Toast.makeText(this, "TÜM İZİNLER ALINDI", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupShareViewPager() {
        var myPagerAdapter = SharePagerAdapter(this)
        binding.viewPager.adapter = myPagerAdapter
        binding.viewPager.currentItem = 1
        binding.viewPager.offscreenPageLimit = 1

        TabLayoutMediator(binding.tabLayout,binding.viewPager){ tab, position->
            when(position){
                0 -> tab.text = "KAMERA"
                1 -> tab.text = "VİDEO"
                2 -> tab.text = "GALERİ"
                else -> tab.text = "Tab ${position + 1}"
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

            }
        } )



    }

    override fun onBackPressed() {
        binding.mainLayout.visibility= View.VISIBLE
        binding.flShareNextFrame.visibility= View.GONE
        super.onBackPressed()
    }
}



