package com.example.kotlininstagramapp.Home

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Notification
import com.example.kotlininstagramapp.R

class NotificationAdapter(var mContext : Context, private val notificationList: List<Notification>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_FOLLOW_REQUEST = 1
        private const val VIEW_TYPE_POST_LIKE = 2
        private const val VIEW_TYPE_COMMENT = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FOLLOW_REQUEST -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification_follow_request, parent, false)
                FollowRequestViewHolder(view)
            }
            VIEW_TYPE_POST_LIKE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_notification_like, parent, false)
                PostLikeViewHolder(view)
            }
            VIEW_TYPE_COMMENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_notification_comment, parent, false)
                CommentViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = notificationList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_FOLLOW_REQUEST -> {
                val followRequestHolder = holder as FollowRequestViewHolder
                followRequestHolder.bind(currentItem)
            }
            VIEW_TYPE_POST_LIKE -> {
                val postLikeHolder = holder as PostLikeViewHolder
                // Bind data or set listeners for post like view holder
            }
            VIEW_TYPE_COMMENT -> {
                val commentHolder = holder as CommentViewHolder
                // Bind data or set listeners for comment view holder
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (notificationList[position].type) {
            "follow_request" -> VIEW_TYPE_FOLLOW_REQUEST
            "post_like" -> VIEW_TYPE_POST_LIKE
            "comment" -> VIEW_TYPE_COMMENT
            else -> throw IllegalArgumentException("Invalid notification type")
        }
    }

    inner class FollowRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.notification_profileImage)
        val notificationText: TextView = itemView.findViewById(R.id.notification_text)
        val confirmCard: CardView = itemView.findViewById(R.id.notification_confirm)
        val deleteCard: CardView = itemView.findViewById(R.id.notification_delete)

        fun bind(currentItem: Notification) {
            val time :String = currentItem.timestamp.toDate().toString()
            val userName :String = currentItem.userName
            val text ="${userName} seni takip etmek istiyor ${time}"
            formatTextView(notificationText,text)

            Glide.with(mContext).load(currentItem.profileImage).into(profileImage)

            confirmCard.setOnClickListener {

            }

            deleteCard.setOnClickListener {

            }


        }


    }

    class PostLikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }



    fun formatTextView(textView: TextView, text: String) {
        val words = text.trim().split(" ")

        if (words.isNotEmpty()) {
            val spannableStringBuilder = SpannableStringBuilder()
            for (i in words.indices) {
                val word = words[i]

                val isBold = i == 0
                val isLastWord = i == words.size - 1

                val spannableWord = SpannableStringBuilder("$word ")
                if (isBold) {
                    spannableWord.setSpan(StyleSpan(Typeface.BOLD), 0, word.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                if (isLastWord) {
                    spannableWord.setSpan(ForegroundColorSpan(Color.parseColor("#7F7F7F")), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                spannableStringBuilder.append(spannableWord)
            }

            textView.text = spannableStringBuilder
        }
    }
}





