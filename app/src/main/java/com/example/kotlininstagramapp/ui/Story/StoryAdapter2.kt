package com.example.kotlininstagramapp.ui.Story

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.Home.HomeFragment
import com.example.kotlininstagramapp.Models.Story
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.ui.Login.LoginState
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter2(private val context: HomeFragment, private val stories: List<Story>) :
    RecyclerView.Adapter<StoryAdapter2.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: CircleImageView = itemView.findViewById(R.id.iv_storyFollowedUser)
        private val usernameText: TextView = itemView.findViewById(R.id.tv_storyFollowedUser)

        fun bind(story: Story) {
            // Set profile image and username
            usernameText.text = story.fullName
            Glide.with(itemView.context).load(story.profilePicture).into(profileImage)

            // Show add button if this is the current user and they have no stories
            if (story.username == UserSingleton.userModel!!.userName) {
                if (story.stories.isEmpty()) {
                    // Display add button
                    // Add your logic to show add button
                } else {
                    // Hide add button
                }
            }
        }
    }
}
