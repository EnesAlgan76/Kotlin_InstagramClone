package com.example.kotlininstagramapp.Share

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView),2)
        println("******* share activity")
    }
}