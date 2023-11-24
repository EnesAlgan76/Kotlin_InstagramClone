package com.example.kotlininstagramapp.Search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R

class SearchResultsAdapter(context:Context) : RecyclerView.Adapter<SearchResultsAdapter.UserViewHolder>() {

    private var userList: List<Map<String, String>> = listOf()
    val mContext =context



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
        // Add other views for profile image if needed

        fun bind(user: Map<String, String>) {
            userNameTextView.text = user["userName"]
            fullNameTextView.text = user["userFullName"]
            Glide.with(mContext).load(user["userProfileImage"]).into(profileImage)

            // Set profile image if available in the map
            // profileImageView.setImageURI(Uri.parse(user["profileimage"]))
        }
    }
}
