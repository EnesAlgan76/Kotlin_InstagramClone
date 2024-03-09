package com.example.kotlininstagramapp.Generic

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Home.SinglePostListFragment
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.Profile.ProfileUserPostsAdapter
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityUserDetailPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserExplorePage : AppCompatActivity(),FollowStateUIHandler, OnSinglePostItemClicked {
    lateinit var followStateButton : Button
    private var userId: String? = null
    lateinit var binding : ActivityUserDetailPageBinding
    lateinit var userPostItems: ArrayList<UserPostItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailPageBinding.inflate(layoutInflater)
        userId = intent.getStringExtra("USER_ID")

        CoroutineScope(Dispatchers.Main).launch{

            val user = withContext(Dispatchers.IO){
                FirebaseHelper().getUserById(userId!!)!!
            }
            setUserInfos(user)

            val isFollowing =  withContext(Dispatchers.IO){FirebaseHelper().isUserFollowing(userId?:"")}
            handleFollowStateUI(isFollowing)

            if(isFollowing){
                showPosts(user)
            }else{
                showPrivateAccountInfo()
            }

        }



        binding.userExploreBtnFollow.setOnClickListener{
            if (userId != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        FirebaseHelper().sendFollowRequest(userId!!)
                    }
                    val isFollowed = FirebaseHelper().isUserFollowing(userId?:"") // gizli hesap değilse anında takip edilir. gizli ise isek gönderildi yazısı göster
                    handleFollowStateUI(isFollowed)

                    if(!isFollowed){
                        binding.userExploreBtnFollow.visibility = View.INVISIBLE
                        binding.userExploreLayoutFollowandmessage.visibility = View.INVISIBLE
                        binding.userExploreBtnFollowRequestSended.visibility = View.VISIBLE
                    }



                }

            }
        }

        binding.userExploreBtnFollowOptions.setOnClickListener{
            val bottomSheetFragment = FollowOptionsBottomSheetFragment(userId!!,this)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
        setContentView(binding.root)
    }



    private fun showPrivateAccountInfo() {
        binding.imageViewPrivateInfo.visibility = View.VISIBLE
    }

    private fun showPosts(user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO){
                userPostItems = FirebaseHelper().fetchUserPosts(user)
            }
            setUserInfos(user)
            setRecycleView(userPostItems)
        }
    }
    private fun setUserInfos(user: User) {

        binding.userExploreTvUserName.setText(user.userName)
        Glide.with(this).load(user.userDetails.profilePicture).error(R.drawable.icon_profile).placeholder(R.drawable.icon_profile).into(binding.userExploreIvProfile)
        binding.userExploreTvName.setText(user.userFullName)
        binding.userExploreTvBiograpy.setText(user.userDetails.biography)
        binding.userExploreTvFollow.setText(user.userDetails.following.toString())
        binding.userExploreTvFollowers.setText(user.userDetails.follower.toString())
        binding.userExploreTvPosts.setText(user.userDetails.post.toString())
    }

    private fun setRecycleView(userPostItems: ArrayList<UserPostItem>) {
        var adapter = ProfileUserPostsAdapter(context = this, listOf())
        binding.userExploreRvProfilePageUserPosts.adapter = adapter
        binding.userExploreRvProfilePageUserPosts.layoutManager = GridLayoutManager(this, 3)
    }

    override fun handleFollowStateUI(isFollowing: Boolean) {
        if(isFollowing){
            binding.userExploreLayoutFollowandmessage.visibility = View.VISIBLE
            binding.userExploreBtnFollow.visibility = View.INVISIBLE
        }else{
            binding.userExploreBtnFollow.visibility = View.VISIBLE
            binding.userExploreLayoutFollowandmessage.visibility = View.INVISIBLE
        }
    }

    override fun onSingleItemClicked(position:Int) {
        binding.userExploreScrollView2.visibility = View.INVISIBLE
        binding.userExploreFlActivityProfile.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().replace(R.id.userExplore_fl_activity_profile, SinglePostListFragment(userPostItems,position)).commit()

    }


}

