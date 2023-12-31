package com.example.kotlininstagramapp.Home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserExplorePage
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.TextHighlighter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit


class PostsAdapter(private var posts: ArrayList<UserPostItem>, private val mContext: Context, private val fragmentManager: FragmentManager, private val recyclerView: RecyclerView
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {
    private val defaultImage = R.drawable.icon_profile
    private var playPosition = -2 // Aktif olarak oynatılan öğenin pozisyonunu saklar
    private val handler = Handler(Looper.getMainLooper())

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItemPosition !=-1 && playPosition!=firstVisibleItemPosition){
                    playPosition = firstVisibleItemPosition
                    if(posts[playPosition].userPostUrl.contains("videos")){
                        handler.removeCallbacksAndMessages(null)
                        handler.post {

                            val holderTop = recyclerView.findViewHolderForAdapterPosition(playPosition-1) as? PostViewHolder
                            holderTop?.post_vv_postvideo?.pause()

                            val holderCenter = recyclerView.findViewHolderForAdapterPosition(playPosition) as? PostViewHolder
                            holderCenter?.post_vv_postvideo?.start()

                            val holderBottom = recyclerView.findViewHolderForAdapterPosition(playPosition+1) as? PostViewHolder
                            holderBottom?.post_vv_postvideo?.pause()
                        }
                    }

                }

            }
        })
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val userPostItem = posts[position]
        with(holder) {
            fullNameTextView.text = userPostItem.userFullName
            post_tvusername.text = userPostItem.userName
            post_tvdescription.text = userPostItem.postDescription
            TextHighlighter.highlightWordsTextView(post_tvdescription)
            post_tv_dateago.text = getTimeAgo(userPostItem.yuklenmeTarihi.toLong())
            post_tv_likecount.text = "${userPostItem.likeCount} beğenme"

            showComment.setOnClickListener {
                val bottomSheetFragment = CommentBottomSheetFragment(userPostItem.postId, userPostItem.userId, userPostItem.userPostUrl)
                bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
            }

            fullNameTextView.setOnClickListener {
                val intent = Intent(mContext, UserExplorePage::class.java).apply {
                    putExtra("USER_ID", userPostItem.userId)
                }
                mContext.startActivity(intent)
            }

            updateLikeButton(holder, userPostItem)
            setLikeClickListener(holder, userPostItem, position)
        }

        Glide.with(mContext).load(userPostItem.profilePicture).placeholder(defaultImage).error(defaultImage).into(holder.post_profileimage)

        loadMedias(holder, userPostItem)
    }


    private fun loadMedias(holder: PostViewHolder, userPostItem: UserPostItem) {
        if(userPostItem.userPostUrl.contains("videos")){
            Glide.with(mContext)
                .load(userPostItem.userPostUrl)
                .into(holder.post_iv_postimage)

            holder.post_vv_postvideo.visibility = View.VISIBLE
            val videoView = holder.post_vv_postvideo
            videoView.setVideoURI(Uri.parse(userPostItem.userPostUrl))

            videoView.setOnPreparedListener { mediaPlayer ->
                holder.post_iv_postimage.visibility = View.GONE
                Log.e("video Hazır",holder.post_tvdescription.text.toString())
                mediaPlayer.start()

            }

        }else{
            holder.post_vv_postvideo.visibility = View.GONE
            holder.post_iv_postimage.visibility = View.VISIBLE
            Glide.with(mContext)
                .load(userPostItem.userPostUrl)
                .into(holder.post_iv_postimage)
        }

    }







    private fun updateLikeButton(holder: PostViewHolder, userPostItem: UserPostItem) {
        CoroutineScope(Dispatchers.Main).launch {
            val isLiked = withContext(Dispatchers.IO) {
                FirebaseHelper().isPostLiked(userPostItem.postId)
            }
            holder.post_ivlike.setImageResource(
                if (isLiked) R.drawable.heart_red else R.drawable.heart
            )
        }
    }

    private fun setLikeClickListener(
        holder: PostViewHolder,
        userPostItem: UserPostItem,
        position: Int
    ) {
        var lastClickTime: Long = 0
        holder.post_ivlike.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > 1000) {
                CoroutineScope(Dispatchers.IO).launch {
                    val auth: FirebaseAuth = FirebaseAuth.getInstance()
                    val userDocumentRef = FirebaseFirestore.getInstance().collection("users")
                        .document(auth.currentUser!!.uid)
                    val likedPostDocRef = userDocumentRef.collection("liked_posts")
                        .document(userPostItem.postId)
                    val document = likedPostDocRef.get().await()
                    val tempPost = posts[position]
                    if (document.exists()) {
                        withContext(Dispatchers.Main) {
                            userPostItem.likeCount = (userPostItem.likeCount.toInt() - 1).toString()
                            posts[position] = tempPost
                            notifyItemChanged(position)
                        }
                        likedPostDocRef.delete().await()
                        FirebaseHelper().updateLikeCount(userPostItem.postId, userPostItem.userId, false)
                    } else {
                        withContext(Dispatchers.Main) {
                            tempPost.likeCount = (tempPost.likeCount.toInt() + 1).toString()
                            posts[position] = tempPost
                            notifyItemChanged(position)
                        }
                        val data = mapOf("post_id" to userPostItem.postId)
                        likedPostDocRef.set(data).await()
                        FirebaseHelper().updateLikeCount(userPostItem.postId, userPostItem.userId, true)
                        FirebaseHelper().sendLikeNotification(userPostItem.userId, userPostItem.userPostUrl)
                    }
                }
            } else {
                println("----- >> Çift Tıklandı")
            }
            lastClickTime = currentTime
        }
    }



    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTextView: TextView = itemView.findViewById(R.id.post_tv_fullname)
        val post_tv_dateago: TextView = itemView.findViewById(R.id.post_tv_dateago)
        val post_profileimage: CircleImageView = itemView.findViewById(R.id.post_profileimage)
        val post_iv_postimage: ImageView = itemView.findViewById(R.id.post_iv_postimage)
        val post_vv_postvideo: VideoView = itemView.findViewById(R.id.post_vv_postvideo)
        val post_tvusername: TextView = itemView.findViewById(R.id.post_tvusername)
        val post_tvdescription: TextView = itemView.findViewById(R.id.post_tvdescription)
        val showComment: TextView = itemView.findViewById(R.id.tv_showcomments)
        val post_ivlike: ImageView = itemView.findViewById(R.id.post_ivlike)
        val post_tv_likecount: TextView = itemView.findViewById(R.id.post_tv_likecount)
    }

    fun getTimeAgo(millis: Long): String {
        val currentTime = System.currentTimeMillis()
        var diffInMillis = currentTime - millis
        if (diffInMillis < 0) diffInMillis = 0

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
