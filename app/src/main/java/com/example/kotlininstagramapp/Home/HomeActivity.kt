package com.example.kotlininstagramapp.Home

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlininstagramapp.Login.LoginActivity
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.databinding.ActivityHomeBinding
import com.example.kotlininstagramapp.utils.MyPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var binding : ActivityHomeBinding
    val MYCAMERA_PERMISSION_CODE =1001
    var auth = FirebaseAuth.getInstance()
    var firebaseHelper: FirebaseHelper = FirebaseHelper()
    var allPosts: ArrayList<UserPostItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.extras != null){
            startActivity(Intent(this,NotificationsActivity::class.java))  // if clicked on notificcation
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (auth.currentUser!=null){
            Toast.makeText(this, "registered", Toast.LENGTH_SHORT).show()
            setupHomeViewPager()
        }else{
            Toast.makeText(this, "unregistered", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun setupHomeViewPager() {
        var myPagerAdapter = MyPagerAdapter(this)
        binding.viewPager.adapter = myPagerAdapter
        binding.viewPager.currentItem = 1

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position==0){
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, binding.mainContainer).let { controller ->
                        controller.hide(WindowInsetsCompat.Type.systemBars())
                        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }else{
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                    WindowInsetsControllerCompat(window, binding.mainContainer).show(WindowInsetsCompat.Type.systemBars())

                }
                super.onPageSelected(position)
            }
        })
    }



    val permissionLauncher =registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted->
        if (isGranted){
            Log.e("-----------","Kamera İzni Verilmiş")
        }else{
            Log.e("-----------","Kamera İzni Verilmemiş")
            binding.viewPager.setCurrentItem(1)
        }
    }

    override fun onBackPressed() {
        if(binding.viewPager.currentItem!=1){
            binding.viewPager.currentItem =1
        }else{
            super.onBackPressed()
        }
    }


 /*private fun requestCameraPermission() {
     if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
         ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MYCAMERA_PERMISSION_CODE)
         Log.e("-----------","Kamera İzni Verilmemiş")
     }

 }

 override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
     super.onRequestPermissionsResult(requestCode, permissions, grantResults)
     if (requestCode == MYCAMERA_PERMISSION_CODE) {
         if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             Log.e("-----------","Kamera İzni Verildi")
         } else {
             Log.e("-----------","Kamera İzni Verilmedi")
         }
     }
 }*/



}



