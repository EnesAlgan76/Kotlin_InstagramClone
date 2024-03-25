package com.example.kotlininstagramapp.Home

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Conversation
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import kotlinx.coroutines.*

class ConversationsAdapter(private var conversationList: ArrayList<Conversation>) :
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
        var userProfileImage: ImageView = itemView.findViewById(R.id.image_user_profile)
        var userFullName: TextView = itemView.findViewById(R.id.text_user_full_name)
        var lastMessage: TextView = itemView.findViewById(R.id.text_last_message)
        var bluePoint: CardView = itemView.findViewById(R.id.cardView_bluePoint)

        fun bind(conversation: Conversation) {
            CoroutineScope(Dispatchers.IO).launch {
                val user: User? = FirebaseHelper().getUserById(conversation.user_id)
                if (user!=null){
                    withContext(Dispatchers.Main){
                        userFullName.text = user.userFullName
                        lastMessage.text =conversation.last_message

                        if (!conversation.isRead){
                            userFullName.setTextColor(Color.BLACK)
                            lastMessage.setTextColor(Color.BLACK)
                            bluePoint.visibility = View.VISIBLE
                        }else{
                            userFullName.setTextColor(Color.GRAY)
                            lastMessage.setTextColor(Color.GRAY)
                            bluePoint.visibility = View.INVISIBLE
                        }

                        Glide.with(itemView.context).load(user.userDetails.profilePicture).into(userProfileImage)

                        itemView.setOnClickListener {
                            FirebaseHelper().updateConversationReadState(isRead=true,conversationId= conversation.conversation_id)
                            val intent = Intent(itemView.context, ChatActivity::class.java)
                            intent.putExtra("USER_ID",conversation.user_id)
                            intent.putExtra("CONVERSATION_ID",conversation.conversation_id)
                            intent.putExtra("FULL_NAME",user.userFullName)
                            intent.putExtra("PROFILE_IMAGE",user.userDetails.profilePicture)
                            intent.putExtra("USER_NAME",user.userName)
                            itemView.context.startActivity(intent)
                        }
                    }

                }
            }


        }
    }
}
