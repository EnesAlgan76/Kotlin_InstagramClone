package com.example.kotlininstagramapp.Profile

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.Models.UserPost
import com.example.kotlininstagramapp.R
import java.io.File
import java.util.concurrent.TimeUnit


class ProfileUserPostsAdapter(private val context: Context, private val posts: List<UserPost>) : RecyclerView.Adapter<ProfileUserPostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_userposts_image_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userPost = posts[position]
        val isVideo = userPost.userPostUrl?.contains("videos")?:false

        holder.bindData(userPost, isVideo)
        holder.itemView.setOnClickListener {

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv_userpost: ImageView = itemView.findViewById(R.id.iv_userpost)
        private val iv_reel: ImageView = itemView.findViewById(R.id.iv_reel)

        fun bindData(userPost: UserPost, isVideo: Boolean) {
            if (isVideo) {
                Glide.with(context).load(userPost.userPostUrl).into(iv_userpost)
                iv_reel.visibility=View.VISIBLE
            } else {
                Glide.with(context).load(userPost.userPostUrl).into(iv_userpost)
            }
        }
    }




    private fun getVideoThumbnail(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        return retriever.frameAtTime
    }
}
