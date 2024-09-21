package com.example.kotlininstagramapp.Home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserExplorePage
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.ui.Search.SearchResultsAdapter
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.greenrobot.eventbus.EventBus


class ConversationsSearchResultsAdapter(var fragment: ConversationsFragment) : RecyclerView.Adapter<ConversationsSearchResultsAdapter.UserViewHolder>() {

    private var userList: List<Map<String, String>> = listOf()
    val mContext =fragment.requireContext()



    fun setUsers(users: List<Map<String, String>>) {
        userList = users
        notifyDataSetChanged()
    }

    fun clear() {
        userList = listOf()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }



    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.tv_search_username)
        private val fullNameTextView: TextView = itemView.findViewById(R.id.tv_search_fullname)
        private val profileImage: ImageView = itemView.findViewById(R.id.iv_searchprofile)
        val currentUser = FirebaseAuth.getInstance().currentUser


        fun bind(user: Map<String, String>) {
            userNameTextView.text = user["userName"]
            fullNameTextView.text = user["userFullName"]
            Glide.with(mContext).load(user["userProfileImage"]).into(profileImage)

            itemView.setOnClickListener {
                val userId =  user["userId"]
                if(userId != currentUser!!.uid){
                    CoroutineScope(Dispatchers.IO).launch {
                        //val conversation_id = FirebaseHelper().createNewConversation(userId!!,user["userName"]!!,user["userProfileImage"]!!,user["userFullName"]!!,"example Message")

                        val conversation_id: String = FirebaseHelper().getConversationId(userId= userId!!)

                        withContext(Dispatchers.Main){
                            val bundle = Bundle().apply {
                                putString("USER_ID",userId)
                                putString("FULL_NAME",user["userFullName"])
                                putString("PROFILE_IMAGE",user["userProfileImage"])
                                putString("USER_NAME",user["userName"])
                                putString("USER_NAME",user["userName"])
                                putString("CONVERSATION_ID",conversation_id)
                            }

                            fragment.findNavController().navigate(R.id.chatFragment, bundle)
                        }



                    }

                }

            }
            // Set profile image if available in the map
            // profileImageView.setImageURI(Uri.parse(user["profileimage"]))
        }
    }
}
