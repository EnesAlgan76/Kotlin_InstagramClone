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
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserExplorePage
import com.example.kotlininstagramapp.Models.Story
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.ui.Story.StoryAdapter
import com.example.kotlininstagramapp.data.model.HomePagePostItem
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.TextHighlighter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


class PostsAdapter(
    private var posts: ArrayList<HomePagePostItem>,
    private val mContext: Context,
    private val fragmentManager: FragmentManager,
    private val recyclerView: RecyclerView,

    ) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val defaultImage = R.drawable.icon_profile
    private val handler = Handler(Looper.getMainLooper())

    private val VIEW_TYPE_HORIZONTAL_LIST = 1
    private val VIEW_TYPE_VERTICAL_ITEM = 2

    var playingVideoList: MutableList<PostViewHolder> = mutableListOf()

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItemPosition !=-1){
                    if(posts[firstVisibleItemPosition].content.contains("videos")){
                        handler.removeCallbacksAndMessages(null)
                        handler.post {
                            val holderTop = recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition-1) as? PostViewHolder
                            holderTop?.post_vv_postvideo?.pause()

                            val holderCenter = recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition) as? PostViewHolder
                            holderCenter?.post_vv_postvideo?.start()

                            val holderBottom = recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition+1) as? PostViewHolder
                            holderBottom?.post_vv_postvideo?.pause()
                        }
                    }else{
                        handler.post{
                            val holderBottom = recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition+1) as? PostViewHolder
                            holderBottom?.post_vv_postvideo?.pause()

                            val holderTop = recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition-1) as? PostViewHolder
                            holderTop?.post_vv_postvideo?.pause()

                        }

                    }

                }

            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HORIZONTAL_LIST else VIEW_TYPE_VERTICAL_ITEM
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_TYPE_HORIZONTAL_LIST) {
            val horizontalListView =
                inflater.inflate(R.layout.item_storieslist, parent, false)
            StoriesViewHolder(horizontalListView)
        } else {
            val verticalItemView =
                inflater.inflate(R.layout.card_post, parent, false)
            PostViewHolder(verticalItemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        if (viewType == VIEW_TYPE_HORIZONTAL_LIST) {
            val horizontalViewHolder = holder as StoriesViewHolder
            horizontalViewHolder.bind()
        } else {
            val verticalViewHolder = holder as PostViewHolder
            val userPostItem = posts[position]
            with(verticalViewHolder) {
                fullNameTextView.text = userPostItem.userFullName
                post_tvusername.text = userPostItem.userName
                post_tvdescription.text = userPostItem.postDescription
                TextHighlighter.highlightWordsTextView(post_tvdescription)
                post_tv_dateago.text = getTimeAgo(userPostItem.creationDate.toLong())
                post_tv_likecount.text = "${userPostItem.likeCount} beğenme"

                showComment.setOnClickListener {
                    val bottomSheetFragment = CommentBottomSheetFragment(userPostItem.postId, userPostItem.userId, userPostItem.content)

                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
                }

                fullNameTextView.setOnClickListener {
                    val intent = Intent(mContext, UserExplorePage::class.java).apply {
                        putExtra("USER_ID", userPostItem.userId)
                    }
                    mContext.startActivity(intent)
                }

               // updateLikeButton(holder, userPostItem)
               // setLikeClickListener(holder, userPostItem, position)
            }

            Glide.with(mContext).load(userPostItem.userProfileImage).placeholder(defaultImage).error(defaultImage).into(holder.post_profileimage)

            loadMedias(holder, userPostItem)
        }



    }

    override fun getItemCount(): Int = posts.size

    private fun loadMedias(holder: PostViewHolder, userPostItem: HomePagePostItem) {
        if(userPostItem.content.contains("videos")){
            Glide.with(mContext)
                .load(userPostItem.content)
                .into(holder.post_iv_postimage)

            holder.post_vv_postvideo.visibility = View.VISIBLE
            val videoView = holder.post_vv_postvideo
            videoView.setVideoURI(Uri.parse(userPostItem.content))

            videoView.setOnPreparedListener { mediaPlayer ->
                holder.post_iv_postimage.visibility = View.GONE
                Log.e("video Hazır",holder.post_tvdescription.text.toString())
            }

        }else{
            holder.post_vv_postvideo.visibility = View.GONE
            holder.post_iv_postimage.visibility = View.VISIBLE
            Glide.with(mContext)
                .load(userPostItem.content)
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

    private fun setLikeClickListener(holder: PostViewHolder, userPostItem: UserPostItem, position: Int) {
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
                    if (document.exists()) {   // zaten beğenilmişse kaldır
                        withContext(Dispatchers.Main) {
                            userPostItem.likeCount = (userPostItem.likeCount.toInt() - 1).toString()
                            posts[position] = tempPost
                            notifyItemChanged(position)
                        }
                        likedPostDocRef.delete().await()
                        FirebaseHelper().updateLikeCount(userPostItem.postId, userPostItem.userId, false)
                        FirebaseHelper().deleteLikeNotification(userPostItem.userId, userPostItem.userPostUrl)
                    } else {   // ilk defa begenilecek
                        withContext(Dispatchers.Main) {
                            tempPost.likeCount = (tempPost.likeCount + 1)
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

    inner class StoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val horizontalRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_homeFragmentStories)
        private val horizontalAdapter = StoryAdapter(context = mContext, listOf())

        init {
            horizontalRecyclerView.adapter = horizontalAdapter
            val layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            horizontalRecyclerView.layoutManager = layoutManager
        }

        fun bind() {
            CoroutineScope(Dispatchers.IO).launch {
                var stories: List<Story> = FirebaseHelper().getFollowedUsersStories()
                withContext(Dispatchers.Main){
                    horizontalAdapter.setData(stories)
                    EventBus.getDefault().postSticky(EventBusDataEvents.SendStories(stories))
                }

            }
            //var stories : ArrayList<Story> = FirebaseHelper().getStoriesFollowedUsers()

        }
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
