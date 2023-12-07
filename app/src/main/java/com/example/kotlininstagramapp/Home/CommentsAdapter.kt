package com.example.kotlininstagramapp.Home

import Comment
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class CommentsAdapter(var mContext:Context,var commentMapList: ArrayList<Pair<Comment,Boolean>>) :RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var tek_satir:View = LayoutInflater.from(parent.context).inflate(R.layout.card_comment,parent,false)
        return MyViewHolder(tek_satir)
    }

    override fun getItemCount(): Int {
        return commentMapList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var (comment, isLiked) = commentMapList[position]

        Log.e("COMMENTS",comment.toString())
        holder.comment.text = comment.comment
        holder.userName.text = comment.user_name
        holder.timeAgo.text = getTimeAgo(comment.time.toLong())
        holder.likeCount.text = comment.like_count
        Glide.with(mContext).load(comment.user_profile_picture).into(holder.profileImage)

        val likeImageResource = if (isLiked) {
            R.drawable.heart_red
        } else {
            R.drawable.heart
        }

        holder.likeButton.setImageResource(likeImageResource)


        holder.likeButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseHelper().updateCommentLikeState(comment.commentId, comment.like_count.toInt())

            }

            comment.like_count =if (isLiked){(comment.like_count.toInt()-1).toString()}else{(comment.like_count.toInt()+1).toString()}
            commentMapList[position]= (comment to !isLiked)
            isLiked = !isLiked
            notifyItemChanged(position)
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

    fun updateComments(newComment: Comment) {
        commentMapList.add(0,(newComment to false))
        notifyItemInserted(0)
    }

    fun getTimeAgo(millis: Long): String {
        val currentTime = System.currentTimeMillis()
        var diffInMillis = currentTime - millis
        if(diffInMillis<0){diffInMillis =0}

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return when {
            seconds < 60 -> "$seconds s"
            minutes < 60 -> "$minutes m"
            hours < 24 -> "$hours h"
            else -> "$days d"
        }
    }

}
