package com.example.kotlininstagramapp.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlininstagramapp.Models.Notification
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityNotificationsBinding
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.utils.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsActivity : AppCompatActivity() {
    lateinit var binding :ActivityNotificationsBinding
    val mContext = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        BottomNavigationHandler.setupNavigations(this,binding.bottomNavigationView,0)


        CoroutineScope(Dispatchers.Main).launch {
            val notificationList = withContext(Dispatchers.IO){
              // FirebaseHelper().getNotifications()
                DatabaseHelper().getNotifications()
            }
            val adapter = NotificationAdapter(mContext, notificationList)
            binding.notificationList.adapter = adapter
        }


        binding.notificationList.layoutManager = LinearLayoutManager(this)

        setContentView(binding.root)
    }
}