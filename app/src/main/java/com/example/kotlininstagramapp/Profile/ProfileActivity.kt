package com.example.kotlininstagramapp.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlininstagramapp.Generic.OnSinglePostItemClicked
import com.example.kotlininstagramapp.Home.SinglePostListFragment
import com.example.kotlininstagramapp.Models.UserDetails
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityProfileBinding
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.EImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class ProfileActivity : AppCompatActivity(),OnSinglePostItemClicked{
    lateinit var binding: ActivityProfileBinding
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val userId = firebaseAuth.currentUser!!.uid
    var userPostItems: ArrayList<UserPostItem> = arrayListOf()

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

    private suspend fun setRecycleView() {
        withContext(Dispatchers.IO){
            val user = FirebaseHelper().getUserById(userId)
            try {
                userPostItems = FirebaseHelper().fetchUserPosts(user!!)
            }catch (e:Throwable){
                println("HATAAA ---- >> ${e.message}")
            }
        }
        var adapter = ProfileUserPostsAdapter(context = this,userPostItems)
        binding.rvProfilePageUserPosts.adapter = adapter
        binding.rvProfilePageUserPosts.layoutManager = GridLayoutManager(this, 3)


    }

    private fun setInfos() {
        if(firebaseAuth.currentUser!=null){
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { snapshot ->
                if(snapshot.data!=null){
                    binding.tvUserName.text = snapshot.data?.get("userName").toString()
                    val userDetailsMap = snapshot.data?.get("userDetails") as Map<String, Any>

                    println("-----**---> UserDetails Map: $userDetailsMap")
                    val userDetails:UserDetails = UserDetails.fromMap(userDetailsMap)
                    println("--------> "+snapshot.data?.get("userDetails"))
                    println("----nesene----> "+userDetails.post)
                    binding.tvFollow.text = userDetails.following.toString()
                    binding.tvFollowers.text  = userDetails.follower.toString()
                    binding.tvPosts.text = userDetails.post.toString()
                    binding.tvBiograpy.text = userDetails.biography
                    binding.tvName.text = snapshot.data?.get("userFullName").toString()
                    try {
                        EImageLoader.setImage(userDetails.profilePicture,binding.ivProfile,binding.pbActivityProfile)
                    }catch(e : java.lang.Error){
                        Log.e("------------", "Resim Bulunamadı")
                    }

                    EventBus.getDefault().postSticky(EventBusDataEvents.KullaniciBilgileriGonder(userDetails,binding.tvName.text.toString(),binding.tvUserName.text.toString()))
                }
            }.addOnFailureListener {e ->
                println("*************************" +
                        e.message+
                        "*************************")
            }
        }
    }



    override fun onBackPressed() {
        binding.profileActivityroot.visibility=View.VISIBLE
        println("----- Geri Tuşuna Basıldı ------")
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
            startActivity(Intent(this,SettingActivity::class.java))
        }

        binding.btnEditprofile.setOnClickListener {
            binding.profileActivityroot.visibility= View.GONE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_activity_profile,ProfileEditFragment())
            transaction.addToBackStack("edit profile fragment eklendi 2")
            transaction.commit()
        }
    }

    override fun onSingleItemClicked(position: Int) {
        binding.profileActivityroot.visibility = View.INVISIBLE
        binding.flActivityProfile.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_activity_profile, SinglePostListFragment(userPostItems, position))
            .addToBackStack("SinglePostFragment")
            .commit()
    }


}