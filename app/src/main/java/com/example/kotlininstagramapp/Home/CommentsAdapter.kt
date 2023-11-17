package com.example.kotlininstagramapp.Home

import Comment
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R

class CommentsAdapter(var mContext:Context,var comments: List<Comment>) :RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var tek_satir:View = LayoutInflater.from(parent.context).inflate(R.layout.card_comment,parent,false)
        return MyViewHolder(tek_satir)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var comment:Comment = comments[position]

        holder.comment.text = comment.comment
        holder.userName.text = comment.user_name
        holder.timeAgo.text = comment.time
        holder.likeCount.text = comment.like_count
        Glide.with(mContext).load(comment.user_profile_picture).into(holder.profileImage)

        holder.likeButton.setOnClickListener {

        }


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var comment = itemView.findViewById<TextView>(R.id.tv_comment_comment)
        var profileImage = itemView.findViewById<ImageView>(R.id.iv_comment_profilepicture)
        var userName = itemView.findViewById<TextView>(R.id.tv_comment_userName)
        var timeAgo = itemView.findViewById<TextView>(R.id.tv_comment_timeago)
        var likeButton = itemView.findViewById<ImageView>(R.id.iv_comment_likebutton)
        var likeCount = itemView.findViewById<TextView>(R.id.tv_comment_likecount)
    }

}
