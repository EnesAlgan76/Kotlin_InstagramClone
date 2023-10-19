package com.example.kotlininstagramapp.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlininstagramapp.Models.UserDetails
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityProfileBinding
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.ImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.greenrobot.eventbus.EventBus

class ProfileActivity : AppCompatActivity(){
    lateinit var binding: ActivityProfileBinding
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView_profile),4)
        handleButtonClicks()
        setInfos()

    }

    private fun setInfos() {
        if(firebaseAuth.currentUser!=null){
            val userId = firebaseAuth.currentUser!!.uid
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { snapshot ->
                if(snapshot.data!=null){
                    binding.tvUserName.text = snapshot.data?.get("userName").toString()
                    val userDetails:UserDetails = UserDetails.fromMap(snapshot.data?.get("userDetails") as Map<String, Any>)
                    binding.tvFollow.text = userDetails.following
                    binding.tvFollowers.text  = userDetails.follower
                    binding.tvPosts.text = userDetails.post
                    binding.tvBiograpy.text = userDetails.biography
                    binding.tvName.text = snapshot.data?.get("userFullName").toString()
                    ImageLoader.setImage(userDetails.profilePicture,binding.ivProfile,binding.pbActivityProfile)
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



}