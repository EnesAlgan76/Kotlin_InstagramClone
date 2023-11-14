package com.example.kotlininstagramapp.Home

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlininstagramapp.Login.LoginActivity
import com.example.kotlininstagramapp.Models.UserPost
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.databinding.ActivityHomeBinding
import com.example.kotlininstagramapp.utils.MyPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var binding : ActivityHomeBinding
    val MYCAMERA_PERMISSION_CODE =1001
    var auth = FirebaseAuth.getInstance()
    var firebaseHelper: FirebaseHelper = FirebaseHelper()
    var allPosts: ArrayList<UserPost> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser!=null){
            Toast.makeText(this, "registered", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "unregistered", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }



        binding = ActivityHomeBinding.inflate(layoutInflater)
        setupHomeViewPager()
        setContentView(binding.root)



        //BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView),4)
    }

    private fun setupHomeViewPager() {
        var myPagerAdapter = MyPagerAdapter(this)
        binding.viewPager.adapter = myPagerAdapter
        binding.viewPager.currentItem = 1

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position==0){
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
                super.onPageSelected(position)
            }
        })
    }

    val permissionLauncher =registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted->
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



