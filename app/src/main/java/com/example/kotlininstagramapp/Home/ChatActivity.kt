package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {

    lateinit var binding :ActivityChatBinding
    val firebaseAuth :FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var chatAdapter :ChatAdapter
    var firstMessageSended :Boolean = false
    val layoutManager =LinearLayoutManager(this)

    var printCount = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)

        val  user_id : String = intent.getStringExtra("USER_ID") ?: ""
        val  full_name : String = intent.getStringExtra("FULL_NAME") ?: ""
        val  profile_image : String = intent.getStringExtra("PROFILE_IMAGE") ?: ""
        val  user_name : String = intent.getStringExtra("USER_NAME")?: ""
        val  conversation_id : String = intent.getStringExtra("CONVERSATION_ID")?: ""


        binding.textViewUserFullName.setText(full_name)
        binding.textViewUserName.setText(user_name)
        Glide.with(this).load(profile_image).into(binding.imageViewUserProfile)

        binding.buttonSend.setOnClickListener{
            FirebaseHelper().sendMessage(message = binding.editTextMessage.text.toString(), to =user_id, conversation_id= conversation_id, firstMessageSended)
            firstMessageSended = true
        }






        FirebaseHelper().getMessages(conversation_id,
            onMessagesLoaded = { messages ->
                chatAdapter = ChatAdapter(messages.reversed(), firebaseAuth.currentUser!!.uid)
                binding.recyclerViewMessages.layoutManager = layoutManager
                binding.recyclerViewMessages.adapter = chatAdapter
                binding.recyclerViewMessages.scrollToPosition(chatAdapter.itemCount-1)
            },
            onError = { exception ->
                Log.e("///////////////","Hata Oluştu "+ exception)
            }
        )


        setContentView(binding.root)

    }

    override fun onPause() {
        firstMessageSended = false
        super.onPause()
    }
}