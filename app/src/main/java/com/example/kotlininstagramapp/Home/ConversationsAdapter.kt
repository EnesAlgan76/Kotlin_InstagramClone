package com.example.kotlininstagramapp.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Conversation
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.R

class ConversationsAdapter(private val conversationList: List<Conversation>) :
    RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversationList[position]

        // Bind user data to views in the ViewHolder
        holder.bind(conversation)
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize views from item_user_card layout
        private val userProfileImage: ImageView = itemView.findViewById(R.id.image_user_profile)
        private val userFullName: TextView = itemView.findViewById(R.id.text_user_full_name)
        private val lastMessage: TextView = itemView.findViewById(R.id.text_last_message)

        fun bind(conversation: Conversation) {
            // Set user data to respective views
            // For example:
            userFullName.text = conversation.user_full_name
            lastMessage.text =conversation.last_message// Set last message here

            // Load user profile image using Glide or Picasso
            Glide.with(itemView.context).load(conversation.profile_image).into(userProfileImage)
        }
    }
}
