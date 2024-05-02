package com.example.kotlininstagramapp.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlininstagramapp.Generic.UserSingleton.userModel
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.api.PostApi
import com.example.kotlininstagramapp.data.api.RetrofitInstance
import com.example.kotlininstagramapp.databinding.ActivityProfileBinding
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.EImageLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class ProfileActivity : AppCompatActivity(){
    lateinit var binding: ActivityProfileBinding
    val firebaseAuth = FirebaseAuth.getInstance()
    val userId = firebaseAuth.currentUser!!.uid
    val postService = RetrofitInstance.retrofit.create(PostApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView_profile),4)
        handleButtonClicks()
        setInfos()
        CoroutineScope(Dispatchers.Main).launch {
            setRecycleView()
        }

    }

    private fun setInfos() {
        binding.tvUserName.text = userModel?.userName
        binding.tvFollow.text = userModel?.followingCount.toString()
        binding.tvFollowers.text  = userModel?.followerCount.toString()
        binding.tvPosts.text = userModel?.postCount.toString()
        binding.tvBiograpy.text = userModel?.biography
        binding.tvName.text = userModel?.fullName
        try {
            EImageLoader.setImage(userModel!!.profilePicture,binding.ivProfile,binding.pbActivityProfile)
        }catch(e : java.lang.Error){
            Log.e("------------", "Resim Bulunamadı")
        }

        EventBus.getDefault().postSticky(EventBusDataEvents.KullaniciBilgileriGonder(binding.tvName.text.toString(),binding.tvUserName.text.toString(), userModel!!.biography, userModel!!.profilePicture))
    }

    private suspend fun setRecycleView() {
        withContext(Dispatchers.IO){

            val response = postService.getAllPosts(userId).execute()

            if (response.isSuccessful){
                val postList = response.body()?.data as List<Map<String, Any>>
                if(!postList.isEmpty()){
                    val postDTOList :List<Post> = postList.map { postMap ->
                        Post.fromMap(postMap)
                    }
                    withContext(Dispatchers.Main){
                        val adapter = ProfileUserPostsAdapter(context = this@ProfileActivity,postDTOList)
                        binding.rvProfilePageUserPosts.adapter = adapter
                        binding.rvProfilePageUserPosts.layoutManager = GridLayoutManager(this@ProfileActivity, 3)
                    }

                }
            }
        }



    }




    override fun onBackPressed() {
        super.onBackPressed()
        binding.profileActivityroot.visibility=View.VISIBLE
        onBackPressedDispatcher.onBackPressed()
    }

    fun toggleProfileRootVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.profileActivityroot.visibility = View.VISIBLE
        } else {
            binding.profileActivityroot.visibility = View.GONE
        }
    }


    private fun handleButtonClicks() {
        binding.ivMenu.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        binding.btnEditprofile.setOnClickListener {
            binding.profileActivityroot.visibility= View.GONE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_activity_profile, ProfileEditFragment())
            transaction.addToBackStack("edit profile fragment eklendi 2")
            transaction.commit()
        }
    }


}