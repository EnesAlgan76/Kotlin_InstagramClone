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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    lateinit var binding :ActivityChatBinding
    val firebaseAuth :FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var chatAdapter :ChatAdapter
    var firstMessageSended :Boolean = false
    val layoutManager =LinearLayoutManager(this)

    lateinit var  user_id : String
    lateinit var  full_name : String
    lateinit var  profile_image : String
    lateinit var  user_name : String
    lateinit var conversation_id :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)

        conversation_id  = intent.getStringExtra("CONVERSATION_ID")?: ""
        println("conversation_id ____>>> ${conversation_id}")
        user_id  = intent.getStringExtra("USER_ID") ?: ""
        full_name  = intent.getStringExtra("FULL_NAME") ?: ""
        profile_image  = intent.getStringExtra("PROFILE_IMAGE") ?: ""
        user_name  = intent.getStringExtra("USER_NAME")?: ""



        binding.textViewUserFullName.setText(full_name)
        binding.textViewUserName.setText(user_name)
        Glide.with(this).load(profile_image).into(binding.imageViewUserProfile)

        binding.buttonSend.setOnClickListener{
            handleSendClick()
        }

        if(conversation_id!="NO_CONVERSATION"){
            Log.e(" _-_-_-_-_-_-_-_-_-_-_- ","Mesajlar Getirildi")
            getMessagesAndLoadMessages()
        }else{
            Log.e(" _-_-_-_-_-_-_-_-_-_-_- ","Mesajlar Getirilmedi Konuşma Başlamadı")
        }

        setContentView(binding.root)

    }


    private fun getMessagesAndLoadMessages() {
        FirebaseHelper().getMessages(conversation_id?:"",
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
    }

    private fun handleSendClick() {
        CoroutineScope(Dispatchers.IO).launch {
            if (conversation_id == "NO_CONVERSATION") {
                conversation_id = FirebaseHelper().createNewConversation(user_id,user_name,profile_image,full_name,binding.editTextMessage.text.toString())
                FirebaseHelper().sendMessage(message = binding.editTextMessage.text.toString(), to =user_id, conversation_id= conversation_id!!, firstMessageSended)
                getMessagesAndLoadMessages()
                Log.e(" _-_-_-_-_-_-_-_-_-_-_- ","Yeni Konuşma oluşturuldu ve ilk mesaj gönderildi")
            }else{
                Log.e(" _-_-_-_-_-_-_-_-_-_-_- ","Mesaj Gönderildi")
                FirebaseHelper().sendMessage(message = binding.editTextMessage.text.toString(), to =user_id, conversation_id= conversation_id!!, firstMessageSended)
                firstMessageSended = true
            }
        }
    }

    override fun onPause() {
        firstMessageSended = false
        super.onPause()
    }
}