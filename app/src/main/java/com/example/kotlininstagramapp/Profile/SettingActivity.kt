package com.example.kotlininstagramapp.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivitySettingBinding
import com.example.kotlininstagramapp.utils.BottomNavigationHandler

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        BottomNavigationHandler.setupNavigations(this,binding.bottomNavigationViewProfileSettings,4)
        fragmentNavigations()
    }

    override fun onBackPressed() {
        binding.profileSettingsRoot.visibility=View.VISIBLE
        super.onBackPressed()
    }

    private fun fragmentNavigations() {
        binding.tvEditprofile.setOnClickListener {
            binding.profileSettingsRoot.visibility = View.GONE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSetingsContainer,ProfileEditFragment())
            transaction.addToBackStack("edit profile fragment eklendi")
            transaction.commit()
        }

        binding.tvLogOut.setOnClickListener {
            binding.profileSettingsRoot.visibility = View.GONE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSetingsContainer,LogOutFragment())
            transaction.addToBackStack("logout fragment eklendi")
            transaction.commit()

        }
    }

    private fun setupToolbar() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}