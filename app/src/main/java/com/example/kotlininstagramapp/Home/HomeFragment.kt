package com.example.kotlininstagramapp.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.Story.StoryAdapter
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
    lateinit var iv_notifications:  ImageView
    lateinit var iv_redPoint:  ImageView
    var allPosts: ArrayList<UserPostItem>  = ArrayList()
    var auth = FirebaseAuth.getInstance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_home,container,false)
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)
        iv_directMessage = view.findViewById(R.id.iv_direct_message)
        iv_notifications = view.findViewById(R.id.iv_notifications)
        iv_redPoint = view.findViewById(R.id.iv_redPoint)
        recyclerView = view.findViewById(R.id.rv_homeFragment_posts)


        BottomNavigationHandler.setupNavigations(requireContext(),bottomNavigationView,4)

        iv_directMessage.setOnClickListener {
            (activity as HomeActivity).binding.viewPager.setCurrentItem(2)
        }

        iv_notifications.setOnClickListener {
            startActivity(Intent(requireContext(),NotificationsActivity::class.java))
            removeNotificationRedPoint()
            updateUiOnNotificationStatusChange()
        }


        setupRecyclerView()

        updateUiOnNotificationStatusChange()

        return view
    }


    private fun updateUiOnNotificationStatusChange() {
        FirebaseHelper().listenForNotificationsAndChanges{
                callback ->
            if (callback){
                showNotificationRedPoint()
            }
        }
    }

    private fun showNotificationRedPoint() {
        iv_redPoint.visibility = View.VISIBLE
    }

    private fun removeNotificationRedPoint() {
        iv_redPoint.visibility = View.INVISIBLE
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    allPosts = FirebaseHelper().getAllPosts()
                }
                val adapter=PostsAdapter(allPosts,requireContext(), requireActivity().supportFragmentManager,recyclerView)
                recyclerView.adapter=adapter
            } catch (e: Exception) {
                println("Error fetching posts: ${e.message}")
            }
        }

    }
}