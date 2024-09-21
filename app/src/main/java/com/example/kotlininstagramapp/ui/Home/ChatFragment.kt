package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var chatAdapter: ChatAdapter
    private var firstMessageSent: Boolean = false
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var userId: String
    private lateinit var fullName: String
    private lateinit var profileImage: String
    private lateinit var userName: String
    private lateinit var conversationId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        conversationId = arguments?.getString("CONVERSATION_ID") ?: ""
        userId = arguments?.getString("USER_ID") ?: ""
        fullName = arguments?.getString("FULL_NAME") ?: ""
        profileImage = arguments?.getString("PROFILE_IMAGE") ?: ""
        userName = arguments?.getString("USER_NAME") ?: ""

        setupViews()
        setupListeners()

        if (conversationId != "NO_CONVERSATION") {
            getMessagesAndLoadMessages()
        } else {
            Log.e("", "Messages not retrieved, conversation not started")
        }

        return binding.root
    }

    private fun setupViews() {
        binding.textViewUserFullName.text = fullName
        binding.textViewUserName.text = userName
        Glide.with(this).load(profileImage).into(binding.imageViewUserProfile)
        layoutManager = LinearLayoutManager(context)
        binding.imageViewBack.setOnClickListener {
            findNavController().popBackStack(R.id.conversationsFragment,false)
        }// Set the layout manager for RecyclerView if applicable
    }

    private fun setupListeners() {
        binding.buttonSend.setOnClickListener {
            handleSendClick(binding.editTextMessage.text.toString())
            binding.editTextMessage.text.clear()
        }

        binding.ivVideocall.setOnClickListener {
            getCameraAndMicPermission {
               // sendConnectionRequest(true)
            }
        }

        binding.ivAudiocall.setOnClickListener {
            getCameraAndMicPermission {
                //sendConnectionRequest(false)
            }
        }
    }

    /*private fun sendConnectionRequest(isVideoCall: Boolean) {
        userId.let { username ->
            mainRepository.sendConnectionRequest(username, true) { success ->
                if (success) {
                    val action = ChatFragmentDirections.actionChatFragmentToCallFragment(username, isVideoCall, true)
                    findNavController().navigate(action)
                }
            }
        }
    }*/

    fun getCameraAndMicPermission(success:()->Unit){
        PermissionX.init(this)
            .permissions(android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO)
            .request{allGranted,_,_ ->
                if (allGranted){
                    success()
                } else{
                    Log.e("Chat Fragment", "getCameraAndMicPermission: $allGranted  ---  camera and mic permission is required")
                }
            }
    }


    private fun getMessagesAndLoadMessages() {
        FirebaseHelper().getMessages(conversationId?:"",
            onMessagesLoaded = { messages ->
                messages.removeLast()
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

    private fun handleSendClick(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (conversationId == "NO_CONVERSATION") {
                conversationId = FirebaseHelper().createNewConversation(userId, binding.editTextMessage.text.toString())
                FirebaseHelper().sendMessage(message = text, to =userId, conversation_id= conversationId, firstMessageSent)
                getMessagesAndLoadMessages()
                Log.e(" _-_-_-_-_-_-_-_-_-_-_- ","Yeni Konuşma oluşturuldu ve ilk mesaj gönderildi")
            }else{
                Log.e(" _-_-_-_-_-_-_-_-_-_-_- ","Mesaj Gönderildi")
                FirebaseHelper().sendMessage(message = text, to =userId, conversation_id= conversationId, firstMessageSent)
                firstMessageSent = true
            }
        }
    }

    override fun onPause() {
        firstMessageSent = false
        super.onPause()
    }

    // Implement other necessary methods like getMessagesAndLoadMessages(), handleSendClick(), getCameraAndMicPermission(), etc.
}



