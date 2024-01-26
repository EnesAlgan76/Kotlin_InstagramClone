package com.example.kotlininstagramapp.Profile

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.OnSinglePostItemClicked
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Models.UserDetails
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.R
import java.io.File


class ProfileUserPostsAdapter(private val context: Context, private val posts: List<UserPostItem>) : RecyclerView.Adapter<ProfileUserPostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_userposts_image_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userPost = posts[position]
        val isVideo = userPost.userPostUrl.contains("videos")
        holder.bindData(userPost, isVideo)
        holder.itemView.setOnClickListener {
            (context as OnSinglePostItemClicked).onSingleItemClicked()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv_userpost: ImageView = itemView.findViewById(R.id.iv_userpost)
        private val iv_reel: ImageView = itemView.findViewById(R.id.iv_reel)

        fun bindData(userPostItem: UserPostItem, isVideo: Boolean) {
            if (isVideo) {
                Glide.with(context).load(userPostItem.userPostUrl).into(iv_userpost)
                iv_reel.visibility=View.VISIBLE
            } else {
                Glide.with(context).load(userPostItem.userPostUrl).into(iv_userpost)
            }
        }
    }




    private fun getVideoThumbnail(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        return retriever.frameAtTime
    }
}
