package com.example.kotlininstagramapp.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.UserPost
import com.example.kotlininstagramapp.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import java.util.concurrent.TimeUnit

class PostsAdapter(private val posts: List<UserPost>, val mContext: Context, val framentManager: FragmentManager) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val userPost = posts[position]
        holder.fullNameTextView.text = userPost.userName
        Glide.with(mContext).load(userPost.userPostUrl).into(holder.post_iv_postimage)
        Glide.with(mContext).load(userPost.profilePicture).into(holder.post_profileimage)
        holder.post_tvusername.text = userPost.userName
        holder.post_tvdescription.text =userPost.postDescription
        holder.post_tv_dateago.text = getTimeAgo(userPost.yuklenmeTarihi!!.toLong())

        holder.showComment.setOnClickListener {
            val bottomSheetFragment = BottomSheetDialog(mContext,R.style.BottomSheetTransparent)
            bottomSheetFragment.apply {
                setContentView(R.layout.fragment_bottom_sheet_comments)
                show()
            }
            //bottomSheetFragment.show(framentManager, bottomSheetFragment.tag)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTextView: TextView = itemView.findViewById(R.id.post_tv_fullname)
        val post_tv_dateago: TextView = itemView.findViewById(R.id.post_tv_dateago)
        val post_profileimage: CircleImageView = itemView.findViewById(R.id.post_profileimage)
        val post_iv_postimage: ImageView = itemView.findViewById(R.id.post_iv_postimage)
        val post_tvusername: TextView = itemView.findViewById(R.id.post_tvusername)
        val post_tvdescription: TextView = itemView.findViewById(R.id.post_tvdescription)
        val showComment :TextView = itemView.findViewById(R.id.tv_showcomments)
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
            seconds < 60 -> "$seconds seconds ago"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            else -> "$days days ago"
        }
    }
}

