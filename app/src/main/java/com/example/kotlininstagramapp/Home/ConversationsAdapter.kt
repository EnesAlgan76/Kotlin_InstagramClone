package com.example.kotlininstagramapp.Home

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Conversation
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConversationsAdapter(private val conversationList: List<Conversation>) :
    RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversationList[position]
        holder.bind(conversation)
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userProfileImage: ImageView = itemView.findViewById(R.id.image_user_profile)
        private val userFullName: TextView = itemView.findViewById(R.id.text_user_full_name)
        private val lastMessage: TextView = itemView.findViewById(R.id.text_last_message)

        fun bind(conversation: Conversation) {
            Log.e("**************** >>>>>",conversation.toString());
            userFullName.text = conversation.user_full_name
            lastMessage.text =conversation.last_message
            Glide.with(itemView.context).load(conversation.profile_image).into(userProfileImage)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("USER_ID",conversation.user_id)
                intent.putExtra("CONVERSATION_ID",conversation.conversation_id)
                intent.putExtra("FULL_NAME",conversation.user_full_name)
                intent.putExtra("PROFILE_IMAGE",conversation.profile_image)
                intent.putExtra("USER_NAME",conversation.user_name)
                itemView.context.startActivity(intent)

            }
        }
    }
}
