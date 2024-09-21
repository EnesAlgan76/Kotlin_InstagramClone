package com.example.kotlininstagramapp.Home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Conversation
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.model.UserModel
import com.example.kotlininstagramapp.utils.DatabaseHelper
import kotlinx.coroutines.*
import javax.inject.Inject

class ConversationsAdapter(
    private val fragment: ConversationsFragment,
    private var conversationList: ArrayList<Conversation>,
    private val databaseHelper: DatabaseHelper
) :
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
                val user: UserModel? = databaseHelper.getUserById(conversation.user_id)
                if (user!=null){
                    withContext(Dispatchers.Main){
                        userFullName.text = user.fullName
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

                        Glide.with(itemView.context).load(user.profilePicture).error(R.drawable.profile).into(userProfileImage)

                        itemView.setOnClickListener {
                            FirebaseHelper().updateConversationReadState(isRead=true,conversationId= conversation.conversation_id)

                            val bundle = Bundle().apply {
                                putString("USER_ID", conversation.user_id)
                                putString("CONVERSATION_ID", conversation.conversation_id)
                                putString("FULL_NAME", user.fullName)
                                putString("PROFILE_IMAGE", user.profilePicture)
                                putString("USER_NAME", user.userName)
                            }

                            fragment.findNavController().navigate(R.id.chatFragment, bundle)

                        }
                    }

                }
            }


        }
    }
}
