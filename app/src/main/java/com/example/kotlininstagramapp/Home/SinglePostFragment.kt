package com.example.kotlininstagramapp.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SinglePostFragment(var userPostItems: ArrayList<UserPostItem>) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_single_post, container, false)
        val recyclerView:RecyclerView = view.findViewById(R.id.rv_single_posts)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)

        val adapter=PostsAdapter(userPostItems,requireContext(), requireActivity().supportFragmentManager,recyclerView)
        recyclerView.adapter=adapter


       return view
    }
}