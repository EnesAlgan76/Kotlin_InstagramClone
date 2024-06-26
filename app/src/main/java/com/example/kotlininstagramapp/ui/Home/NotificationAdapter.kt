package com.example.kotlininstagramapp.Home

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.model.NotificationModel
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationAdapter(var mContext : Context, private val notificationList: List<NotificationModel>) :
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
                postLikeHolder.bind(currentItem)
            }
            VIEW_TYPE_COMMENT -> {
                val commentHolder = holder as CommentViewHolder
                commentHolder.bind(currentItem)
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
        val followingCard: CardView = itemView.findViewById(R.id.notification_following)

        fun bind(currentItem: NotificationModel) {
            val timeAgo = currentItem.time
            val userName :String = currentItem.fromUserName
            val text ="${userName} wants to follow you. ${timeAgo}"
            formatTextView(notificationText,text)

            Glide.with(mContext).load(currentItem.fromUserProfilePicture).into(profileImage)

            confirmCard.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch{
                    followingCard.visibility = View.VISIBLE
                    confirmCard.visibility = View.INVISIBLE
                    deleteCard.visibility = View.INVISIBLE
                    withContext(Dispatchers.IO){
                       // FirebaseHelper().acceptFollowRequest(currentItem.userId, currentItem.id)
                        DatabaseHelper().acceptFollowRequest(currentItem.fromUserId, currentItem.id)
                      //  FirebaseHelper().deleteFollowRequestNotification(currentItem.id)
                    }

                }

            }

            deleteCard.setOnClickListener {
               // FirebaseHelper().deleteFollowRequestNotification(currentItem.id)
                CoroutineScope(Dispatchers.IO).launch{
                    try {
                        DatabaseHelper().deleteNotification(currentItem.id.toInt())
                    } catch (e: Exception) {
                        Log.e("SPRING deleteNotification ERROR: ",e.toString())
                    }
                }

            }


        }


    }

    inner class PostLikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profileImage: ImageView = itemView.findViewById(R.id.notification_like_profileImage)
        val preview: ImageView = itemView.findViewById(R.id.notification_like_preview)
        val notificationText: TextView = itemView.findViewById(R.id.notification_like_text)

        fun bind(currentItem: NotificationModel) {
            val userName :String = currentItem.fromUserName
            val timeAgo = currentItem.time

            val imageOrVideo :String  = if(currentItem.postPreview!!.contains("video")) "video" else "photo"

            val text ="${userName +" liked your "+ imageOrVideo}. ${timeAgo}"
            formatTextView(notificationText,text)

            Glide.with(mContext).load(currentItem.fromUserProfilePicture).into(profileImage)

            Glide.with(mContext).load(currentItem.postPreview).into(preview)



        }
    }



    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profileImage: ImageView = itemView.findViewById(R.id.notification_comment_profileImage)
        val preview: ImageView = itemView.findViewById(R.id.notification_comment_preview)
        val notificationText: TextView = itemView.findViewById(R.id.notification_comment_text)

        fun bind(currentItem: NotificationModel) {
            val userName :String = currentItem.fromUserName

            val timeAgo = currentItem.time

            val imageOrVideo :String  = if(currentItem.postPreview!!.contains("video")) "video" else "photo"

            val text ="${userName +" commented on your "+ imageOrVideo}. ${timeAgo}"
            formatTextView(notificationText,text)

            Glide.with(mContext).load(currentItem.fromUserProfilePicture).into(profileImage)

            Glide.with(mContext).load(currentItem.postPreview).into(preview)



        }
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

    fun formatTimeAgo(timestamp: Timestamp): String {
        val currentTime = Timestamp.now()
        val timeDifferenceSeconds = currentTime.seconds - timestamp.seconds

        return when {
            timeDifferenceSeconds < 60 -> "${timeDifferenceSeconds}s"
            timeDifferenceSeconds < 3600 -> "${timeDifferenceSeconds / 60}m"
            timeDifferenceSeconds < 86400 -> "${timeDifferenceSeconds / 3600}h"
            timeDifferenceSeconds < 2592000 -> "${timeDifferenceSeconds / 86400}d"
            else -> "${timeDifferenceSeconds / 2592000}m"
        }
    }


}





