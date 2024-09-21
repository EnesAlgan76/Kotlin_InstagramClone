package com.example.kotlininstagramapp.Generic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.ui.Profile.ProfileUserPostsAdapter
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.model.UserModel
import com.example.kotlininstagramapp.databinding.FragmentUserExplorePageBinding
import com.example.kotlininstagramapp.utils.DatabaseHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class UserExplorePage : Fragment(), FollowStateUIHandler, OnSinglePostItemClicked {
    private var _binding: FragmentUserExplorePageBinding? = null
    private val binding get() = _binding!!

    private var userId: String? = null
    private lateinit var userPostItems: List<Post>

    @Inject
    lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserExplorePageBinding.inflate(inflater, container, false)
        userId = arguments?.getString("USER_ID")

        CoroutineScope(Dispatchers.Main).launch {
            val user = withContext(Dispatchers.IO) {
                databaseHelper.getUserById(userId!!)!!
            }
            setUserInfos(user)

            val isFollowing = withContext(Dispatchers.IO) {
                databaseHelper.isUserFollowing(userId!!)
            }
            handleFollowStateUI(isFollowing)

            if (isFollowing) {
                showPosts(user)
            } else {
                showPrivateAccountInfo()
            }
        }

        binding.userExploreBtnFollow.setOnClickListener {
            userId?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        databaseHelper.sendFollowRequest(it)
                    }
                    val isFollowed = databaseHelper.isUserFollowing(it)
                    handleFollowStateUI(isFollowed)

                    if (!isFollowed) {
                        binding.userExploreBtnFollow.visibility = View.INVISIBLE
                        binding.userExploreLayoutFollowandmessage.visibility = View.INVISIBLE
                        binding.userExploreBtnFollowRequestSended.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.userExploreBtnFollowOptions.setOnClickListener {
            val bottomSheetFragment = FollowOptionsBottomSheetFragment(userId!!, this)
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        return binding.root
    }

    private fun showPrivateAccountInfo() {
        binding.imageViewPrivateInfo.visibility = View.VISIBLE
    }

    private fun showPosts(user: UserModel) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                userPostItems = databaseHelper.fetchUserPosts(user.userId)
            }
            setRecycleView(userPostItems)
        }
    }

    private fun setUserInfos(user: UserModel) {
        binding.userExploreTvUserName.text = user.userName
        Glide.with(this).load(user.profilePicture)
            .error(R.drawable.profile)
            .placeholder(R.drawable.profile)
            .into(binding.userExploreIvProfile)
        Glide.with(this).load(user.profilePicture)
            .error(R.drawable.profile)
            .placeholder(R.drawable.profile)
            .into(binding.userExploreIvProfileBig)
        binding.userExploreTvName.text = user.fullName
        binding.userExploreTvBiograpy.text = user.biography
        binding.userExploreTvFollow.text = user.followingCount.toString()
        binding.userExploreTvFollowers.text = user.followerCount.toString()
        binding.userExploreTvPosts.text = user.postCount.toString()
    }

    private fun setRecycleView(userPostItems: List<Post>) {
        val adapter = ProfileUserPostsAdapter(context = requireContext(), this, userPostItems)
        binding.userExploreRvProfilePageUserPosts.adapter = adapter
        binding.userExploreRvProfilePageUserPosts.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    override fun handleFollowStateUI(isFollowing: Boolean) {
        if (isFollowing) {
            binding.userExploreLayoutFollowandmessage.visibility = View.VISIBLE
            binding.userExploreBtnFollow.visibility = View.INVISIBLE
        } else {
            binding.userExploreBtnFollow.visibility = View.VISIBLE
            binding.userExploreLayoutFollowandmessage.visibility = View.INVISIBLE
        }
    }

    override fun onSingleItemClicked(position: Int) {
        val bundle = Bundle().apply {
            putInt("post_id", userPostItems[position].postId.toInt())
        }
        findNavController().navigate(R.id.singlePostFragment,bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
