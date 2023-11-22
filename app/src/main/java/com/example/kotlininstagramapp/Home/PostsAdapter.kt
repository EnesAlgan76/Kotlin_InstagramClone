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
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class PostsAdapter(private val posts: List<UserPost>, val mContext: Context, val fragmentManager: FragmentManager) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val userPost = posts[position]
        var isLiked =false


        holder.fullNameTextView.text = userPost.userName
        Glide.with(mContext).load(userPost.userPostUrl).into(holder.post_iv_postimage)
        Glide.with(mContext).load(userPost.profilePicture).into(holder.post_profileimage)
        holder.post_tvusername.text = userPost.userName
        holder.post_tvdescription.text =userPost.postDescription
        holder.post_tv_dateago.text = getTimeAgo(userPost.yuklenmeTarihi!!.toLong())

        holder.showComment.setOnClickListener {
            val bottomSheetFragment = CommentBottomSheetFragment(userPost.postId!!)
            bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
        }

        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.IO){
                isLiked = FirebaseHelper().isPostLiked(userPost.postId!!)
            }
            holder.post_ivlike.setImageResource(
                if(isLiked){R.drawable.heart_red}else{R.drawable.heart}
            )
        }

        var sonTiklama:Long = 0
        holder.post_ivlike.setOnClickListener {
            var ilkTiklamaZamani = System.currentTimeMillis()
            if (ilkTiklamaZamani-sonTiklama>1000){
                FirebaseHelper().saveUserLike(userPost.postId)
                println(isLiked.toString())
                //isLiked=!isLiked
                notifyItemChanged(position)

            }else{
                println("----- >> Çift Tıklandı")
            }
            sonTiklama = ilkTiklamaZamani
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
        val post_ivlike :ImageView = itemView.findViewById(R.id.post_ivlike)
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

