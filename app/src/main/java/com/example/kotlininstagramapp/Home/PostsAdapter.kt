package com.example.kotlininstagramapp.Home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserExplorePage
import com.example.kotlininstagramapp.Models.UserDetails
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class PostsAdapter(var posts: ArrayList<UserPostItem>, val mContext: Context, val fragmentManager: FragmentManager) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    val defaultImage = R.drawable.icon_profile
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val userPostItem = posts[position]
        var isLiked =false


        holder.fullNameTextView.text = userPostItem.userFullName
        Glide.with(mContext).load(userPostItem.userPostUrl).into(holder.post_iv_postimage)
        Glide.with(mContext).load(userPostItem.profilePicture).error(defaultImage).into(holder.post_profileimage)
        holder.post_tvusername.text = userPostItem.userName
        holder.post_tvdescription.text =userPostItem.postDescription
        holder.post_tv_dateago.text = getTimeAgo(userPostItem.yuklenmeTarihi.toLong())
        holder.post_tv_likecount.text =("${userPostItem.likeCount} beğenme")
        holder.showComment.setOnClickListener {
            val bottomSheetFragment = CommentBottomSheetFragment(userPostItem.postId)
            bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
        }

        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.IO){
                isLiked = FirebaseHelper().isPostLiked(userPostItem.postId)
            }
            holder.post_ivlike.setImageResource(
                if(isLiked){R.drawable.heart_red}else{R.drawable.heart}
            )
        }

        holder.fullNameTextView.setOnClickListener {
            val intent = Intent(mContext,UserExplorePage::class.java)
            intent.putExtra("USER_ID",userPostItem.userId)
            intent.getStringExtra("USER_ID")
            mContext.startActivity(intent)
        }

        var sonTiklama:Long = 0
        holder.post_ivlike.setOnClickListener {
            var ilkTiklamaZamani = System.currentTimeMillis()
            if (ilkTiklamaZamani-sonTiklama>1000){
                CoroutineScope(Dispatchers.IO).launch {
                    val auth :FirebaseAuth = FirebaseAuth.getInstance()
                        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(auth.currentUser!!.uid)
                        val likedPostDocRef = userDocumentRef.collection("liked_posts").document(userPostItem.postId)
                        val document = likedPostDocRef.get().await()
                        var tempPost = posts[position]
                        if (document.exists()) {
                            withContext(Dispatchers.Main){
                                userPostItem.likeCount= (userPostItem.likeCount.toInt()-1).toString()
                                posts[position] = tempPost
                                notifyItemChanged(position)
                            }
                            likedPostDocRef.delete().await()
                            FirebaseHelper().updateLikeCount(userPostItem.postId, userPostItem.userId, increase = false)

                        } else {
                            withContext(Dispatchers.Main){
                                tempPost.likeCount= (tempPost.likeCount.toInt()+1).toString()
                                posts[position] = tempPost
                                notifyItemChanged(position)
                            }
                            val data = mapOf("post_id" to userPostItem.postId)
                            likedPostDocRef.set(data).await()
                            FirebaseHelper().updateLikeCount(
                                userPostItem.postId,
                                userPostItem.userId,
                                increase = true
                            )


                        }

                }

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
        val post_tv_likecount :TextView = itemView.findViewById(R.id.post_tv_likecount)
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

