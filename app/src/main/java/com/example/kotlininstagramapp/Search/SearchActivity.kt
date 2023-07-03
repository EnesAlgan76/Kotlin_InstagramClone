package com.example.kotlininstagramapp.Search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("SearchActivity sınıfına gidildi")
        setContentView(R.layout.activity_home)
        BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView),1)
    }
}