package com.example.kotlininstagramapp.Story

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityStoryReviewBinding
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

class StoryReviewActivity : AppCompatActivity() {

    lateinit var gelenDosya : File
    lateinit var binding: ActivityStoryReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true)
    fun onMessageEvent(event: EventBusDataEvents.SendMediaFile) {
        gelenDosya = event.mediaFile
        Glide.with(this).load(gelenDosya).into(binding.ivStoryReview)
    }
}