package com.example.kotlininstagramapp.Story

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryAdapter(private val context: Context, private var data: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_CURRENT_USER = 0
    private val VIEW_TYPE_FOLLOWED_USER = 1

    // Other constants or methods as needed

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CURRENT_USER -> {
                val currentUserView = LayoutInflater.from(context).inflate(R.layout.item_story_current_user, parent, false)
                CurrentUserViewHolder(currentUserView)
            }
            VIEW_TYPE_FOLLOWED_USER -> {
                val followedUserView = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false)
                FollowedUserViewHolder(followedUserView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CurrentUserViewHolder -> {
                holder.bindUserData()
                holder.handleClick()
            }
            is FollowedUserViewHolder -> {
                // Adjust position for the current user item
                val imageUrl = data[position - 1]
                Glide.with(context)
                    .load(imageUrl)
                    .into(holder.iv_storyFollowedUser)

                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        // Add 1 to the item count for the current user view
        return data.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_CURRENT_USER
        } else {
            VIEW_TYPE_FOLLOWED_USER
        }
    }

    fun setData(horizontalItemList: List<String>) {
        data = horizontalItemList
        notifyDataSetChanged()
    }

    inner class CurrentUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_storyCurrentUser: ImageView = itemView.findViewById(R.id.iv_storyCurrentUser)
        val tv_storyCurrentUser: TextView = itemView.findViewById(R.id.tv_storyCurrentUser)
        fun handleClick() {
            iv_storyCurrentUser.setOnClickListener {
                val intent = Intent(context, StoryActivity::class.java)
                intent.putExtra("isCurrentUser",true)
                context.startActivity(intent)
                // Handle click for the current user view (e.g., navigate to the user's own story)
                Toast.makeText(context, "Current User Clicked", Toast.LENGTH_SHORT).show()
            }
        }



        fun bindUserData() {
            CoroutineScope(Dispatchers.IO).launch {
                val user: User? = FirebaseHelper().getUserById(FirebaseAuth.getInstance().currentUser!!.uid)
                if (user!=null){
                    withContext(Dispatchers.Main){
                        Glide.with(context)
                            .load(user.userDetails.profilePicture)
                            .into(iv_storyCurrentUser)

                        tv_storyCurrentUser.setText(user.userName)
                    }
                }
            }
        }
    }

    inner class FollowedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_storyFollowedUser: ImageView = itemView.findViewById(R.id.iv_storyFollowedUser)

        fun bind() {
            iv_storyFollowedUser.setOnClickListener {
                val intent = Intent(context, StoryActivity::class.java)
                intent.putExtra("isCurrentUser",false)
                context.startActivity(intent)

                Toast.makeText(context, "Tıklandı: ${iv_storyFollowedUser.resources.toString()}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
