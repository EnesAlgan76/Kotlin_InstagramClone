package com.example.kotlininstagramapp.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.R

class SinglePostListFragment(var userPostItems: ArrayList<UserPostItem>, var position: Int) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_single_post_list, container, false)
        val recyclerView:RecyclerView = view.findViewById(R.id.rv_single_posts)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)

        val adapter=PostsAdapter(userPostItems,requireContext(), requireActivity().supportFragmentManager,recyclerView)
        recyclerView.adapter=adapter

        recyclerView.layoutManager?.scrollToPosition(position)


       return view
    }
}