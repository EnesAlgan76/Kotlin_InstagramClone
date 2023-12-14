package com.example.kotlininstagramapp.Home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    lateinit var binding :ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)

        val  user_id : String = intent.getStringExtra("USER_ID") ?: ""
        val  full_name : String = intent.getStringExtra("FULL_NAME") ?: ""
        val  profile_image : String = intent.getStringExtra("PROFILE_IMAGE") ?: ""
        val  user_name : String = intent.getStringExtra("USER_NAME")?: ""

        binding.editTextMessage.setText(user_id)
        binding.textViewUserFullName.setText(full_name)
        binding.textViewUserName.setText(user_name)
        Glide.with(this).load(profile_image).into(binding.imageViewUserProfile)
        binding.editTextMessage.setText(user_id)





        setContentView(binding.root)
    }
}