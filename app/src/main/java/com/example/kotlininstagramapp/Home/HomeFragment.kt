package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_home,container,false)
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)
        BottomNavigationHandler.setupNavigations(requireContext(),bottomNavigationView,4)
        return view
    }
}