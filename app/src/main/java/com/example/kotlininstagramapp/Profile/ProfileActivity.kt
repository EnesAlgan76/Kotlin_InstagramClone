package com.example.kotlininstagramapp.Profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityProfileBinding
import com.example.kotlininstagramapp.utils.ImageLoader

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView_profile),4)
        handleButtonClicks()
        setProfileImage()

    }

    private fun setProfileImage() {
        var url :String =   "https://images.unsplash.com/photo-1543373014-cfe4f4bc1cdf?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1348&q=80"
        ImageLoader.setImage(url,binding.ivProfile,binding.pbActivityProfile)
    }

    override fun onBackPressed() {
        binding.profileActivityroot.visibility=View.VISIBLE
        super.onBackPressed()
    }

    private fun handleButtonClicks() {
        binding.ivMenu.setOnClickListener {
            startActivity(Intent(this,SettingActivity::class.java))
        }

        binding.btnEditprofile.setOnClickListener {
            binding.profileActivityroot.visibility= View.GONE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_activity_profile,ProfileEditFragment())
            transaction.addToBackStack("edit profile fragment eklendi 2")
            transaction.commit()
        }
    }
}