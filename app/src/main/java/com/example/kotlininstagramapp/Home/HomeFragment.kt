package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Models.UserPost
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var recyclerView:  RecyclerView
    lateinit var iv_directMessage:  ImageView
    var allPosts: ArrayList<UserPost> = ArrayList()
    var auth = FirebaseAuth.getInstance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_home,container,false)
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)
        BottomNavigationHandler.setupNavigations(requireContext(),bottomNavigationView,4)

        iv_directMessage = view.findViewById(R.id.iv_direct_message)
        iv_directMessage.setOnClickListener {
            (activity as HomeActivity).binding.viewPager.setCurrentItem(2)
        }

        recyclerView = view.findViewById(R.id.rv_homeFragment_posts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    allPosts = FirebaseHelper().getUserPosts(auth.currentUser!!.uid)
                    println("İşlemler Tamam")
                }
                println("All Posts: $allPosts")
                setupRecyclerView(allPosts)


            } catch (e: Exception) {
                println("Error fetching posts: ${e.message}")
            }
        }



        return view
    }

    private fun setupRecyclerView(allPosts: ArrayList<UserPost>) {
        val adapter=PostsAdapter(allPosts,requireContext())
        recyclerView.adapter=adapter
    }
}