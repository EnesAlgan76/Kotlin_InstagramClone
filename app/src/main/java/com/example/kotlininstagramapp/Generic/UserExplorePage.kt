package com.example.kotlininstagramapp.Generic

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.Profile.ProfileUserPostsAdapter
import com.example.kotlininstagramapp.databinding.ActivityUserDetailPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserExplorePage : AppCompatActivity(),FollowStateUIHandler {
    lateinit var followStateButton : Button
    private var userId: String? = null
    lateinit var binding : ActivityUserDetailPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailPageBinding.inflate(layoutInflater)
        userId = intent.getStringExtra("USER_ID")

        handleFollowStateUI()

        CoroutineScope(Dispatchers.Main).launch {
            setRecycleView()
        }

        binding.userExploreBtnFollow.setOnClickListener{
            if (userId != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        FirebaseHelper().followUser(userId!!)
                    }
                    handleFollowStateUI()
                }

            }
        }

        binding.userExploreBtnFollowOptions.setOnClickListener{
            val bottomSheetFragment = FollowOptionsBottomSheetFragment(userId!!,this)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
        setContentView(binding.root)
    }

    private suspend fun setRecycleView() {
        var userPostItems: ArrayList<UserPostItem>
        withContext(Dispatchers.IO){
            userPostItems = FirebaseHelper().fetchUserPosts(userId!!)
        }
        var adapter = ProfileUserPostsAdapter(context = this,userPostItems)
        binding.userExploreRvProfilePageUserPosts.adapter = adapter
        binding.userExploreRvProfilePageUserPosts.layoutManager = GridLayoutManager(this, 3)
    }

    override fun handleFollowStateUI() {
        FirebaseHelper().isUserFollowing(userId!!){
                isFollowing ->
            if(isFollowing){
                binding.userExploreLayoutFollowandmessage.visibility = View.VISIBLE
                binding.userExploreBtnFollow.visibility = View.INVISIBLE
            }else{
                binding.userExploreBtnFollow.visibility = View.VISIBLE
                binding.userExploreLayoutFollowandmessage.visibility = View.INVISIBLE
            }
        }
    }


}

