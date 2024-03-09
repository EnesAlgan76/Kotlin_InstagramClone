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
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.Login.LoginActivity
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.api.BaseResponse
import com.example.kotlininstagramapp.api.RetrofitInstance
import com.example.kotlininstagramapp.api.UserApi
import com.example.kotlininstagramapp.api.UserModel
import com.example.kotlininstagramapp.databinding.ActivityHomeBinding
import com.example.kotlininstagramapp.utils.ConsolePrinter
import com.example.kotlininstagramapp.utils.MyPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeBinding
    var auth = FirebaseAuth.getInstance()
    val userService = RetrofitInstance.retrofit.create(UserApi::class.java)
    val currentUser = auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.extras != null){
            startActivity(Intent(this,NotificationsActivity::class.java))
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (currentUser!=null){
            Toast.makeText(this, "registered", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                val response = userService.getUserById(currentUser.uid).execute()
                if(response.isSuccessful){
                    val baseResponse:BaseResponse? = response.body()
                    if (baseResponse?.data != null) {
                        ConsolePrinter.printYellow("Kullanıcı Veritabanında Bulundu: "+response.body())

                        val userDataJson = Gson().toJson(response.body()?.data)
                        val userModel = Gson().fromJson(userDataJson, UserModel::class.java)
                        UserSingleton.userModel = userModel

                    }else{
                        ConsolePrinter.printYellow("Kullanıcı Veritabanında Bulunamadı: "+response.body())

                        currentUser.delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.e("---------", "User account deleted.")
                                }else{
                                    Log.e("---------", "User account didn't deleted.${task.exception}")
                                    auth.signOut()
                                }
                            }
                        withContext(Dispatchers.Main){
                            goToLoginPage()
                        }

                    }

                }
               // UserSingleton.user = FirebaseHelper().getUserById(auth.currentUser!!.uid)
            }
            setupHomeViewPager()
        }else{
            goToLoginPage()
        }

    }

    fun goToLoginPage(){
        Toast.makeText(this, "unregistered", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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



