package com.example.kotlininstagramapp.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlininstagramapp.Generic.UserSingleton.userModel
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.api.PostApi
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.databinding.FragmentProfileBinding
import com.example.kotlininstagramapp.utils.BottomNavHandler
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.EImageLoader
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser!!.uid
    @Inject
    lateinit var postService: PostApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleButtonClicks()
        setInfos()
        CoroutineScope(Dispatchers.Main).launch {
            setRecycleView()
        }
    }

    private fun setInfos() {
        binding.tvUserName.text = userModel?.userName
        binding.tvFollow.text = userModel?.followingCount.toString()
        binding.tvFollowers.text = userModel?.followerCount.toString()
        binding.tvPosts.text = userModel?.postCount.toString()
        binding.tvBiograpy.text = userModel?.biography
        binding.tvName.text = userModel?.fullName
        try {
            EImageLoader.setImage(userModel!!.profilePicture, binding.ivProfile, binding.pbActivityProfile)
        } catch (e: java.lang.Error) {
            Log.e("------------", "Resim Bulunamadı")
        }

        EventBus.getDefault().postSticky(
            EventBusDataEvents.KullaniciBilgileriGonder(
                binding.tvName.text.toString(),
                binding.tvUserName.text.toString(),
                userModel!!.biography,
                userModel!!.profilePicture
            )
        )
    }

    private suspend fun setRecycleView() {
        withContext(Dispatchers.IO) {
            val response = postService.getAllPosts(userId).execute()

            if (response.isSuccessful) {
                val postList = response.body()?.data as List<Map<String, Any>>
                if (postList.isNotEmpty()) {
                    val postDTOList: List<Post> = postList.map { postMap ->
                        Post.fromMap(postMap)
                    }
                    withContext(Dispatchers.Main) {
                        val adapter = ProfileUserPostsAdapter(context = requireContext(), postDTOList)
                        binding.rvProfilePageUserPosts.adapter = adapter
                        binding.rvProfilePageUserPosts.layoutManager = GridLayoutManager(requireContext(), 3)
                    }
                }
            }
        }
    }

    private fun handleButtonClicks() {
        binding.ivMenu.setOnClickListener {
            //startActivity(Intent(requireContext(), SettingActivity::class.java))
            findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
        }

        binding.btnEditprofile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
            /*binding.profileActivityroot.visibility = View.GONE
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_activity_profile, ProfileEditFragment())
            transaction.addToBackStack("edit profile fragment eklendi 2")
            transaction.commit()*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }


    fun toggleProfileRootVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.profileActivityroot.visibility = View.VISIBLE
        } else {
            binding.profileActivityroot.visibility = View.GONE
        }
    }
}
