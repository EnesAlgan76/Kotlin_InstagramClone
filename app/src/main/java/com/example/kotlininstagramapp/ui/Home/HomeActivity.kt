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
import com.example.kotlininstagramapp.ui.Login.LoginActivity
import com.example.kotlininstagramapp.api.RetrofitInstance
import com.example.kotlininstagramapp.Services.UserApi
import com.example.kotlininstagramapp.api.model.UserModel
import com.example.kotlininstagramapp.databinding.ActivityHomeBinding
import com.example.kotlininstagramapp.utils.MyPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeBinding
    var auth = FirebaseAuth.getInstance()
    val userService = RetrofitInstance.retrofit.create(UserApi::class.java)
    val currentUser = auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.extras != null) {
            startActivity(Intent(this, NotificationsActivity::class.java))
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCurrentUser()
    }

    private fun setupCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserFromDatabase(currentUser.uid)
        } else {
            goToLoginPage()
        }
    }

    private fun fetchUserFromDatabase(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var userData: Any? = null
            try {
                val response = userService.getUserById(userId).execute()
                if (response.isSuccessful) {
                    Log.e("Spring Response", response.body().toString())
                    userData = response.body()?.data
                } else {
                    handleConnectionError("Response unsuccesful")
                }
            } catch (e: Exception) {
                handleConnectionError(e.toString())
            }
            userData?.let { handleUserResponse(it) } //?: handleUserNotFound()
        }
    }


    private fun handleUserResponse(userData: Any) {
        val userModel = toUserModel(userData)
        UserSingleton.userModel = userModel

        runOnUiThread { setupHomeViewPager() }
    }

    private fun handleUserNotFound() {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e("---------", "User account deleted.")
            } else {
                Log.e("---------", "User account didn't delete: ${task.exception}")
                auth.signOut()
            }
            goToLoginPage()
        }
    }

    private fun handleConnectionError(e: String) {
        runOnUiThread {
            Toast.makeText(this@HomeActivity, "Bağlantı Hatası", Toast.LENGTH_SHORT).show()
            Log.e("Hata",e)
            goToLoginPage()
        }
    }

    private fun toUserModel(userData: Any): UserModel {
        val userDataJson = Gson().toJson(userData)
        return Gson().fromJson(userDataJson, UserModel::class.java)
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



