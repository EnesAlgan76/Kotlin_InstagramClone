package com.example.kotlininstagramapp.Story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.databinding.ActivityStoryReviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class StoryReviewActivity : AppCompatActivity() {
    lateinit var gelenDosya :File
    lateinit var binding: ActivityStoryReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val filePath = intent.getStringExtra("FILE_PATH")
        if (filePath != null) {
            println("----------FILE_PATH----- >> ${filePath}")
            gelenDosya = File(filePath)
            Glide.with(this).load(gelenDosya).into(binding.ivStoryReview)
        }

        Glide.with(this).load(UserSingleton.user!!.userDetails.profilePicture).into(binding.ivProfileStoryPreview)

        binding.ivSendStory.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseHelper().uploadStory(this@StoryReviewActivity, gelenDosya)
            }

        }



    }



}