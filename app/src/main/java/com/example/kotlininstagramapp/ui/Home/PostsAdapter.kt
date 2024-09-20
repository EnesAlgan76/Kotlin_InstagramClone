package com.example.kotlininstagramapp.Home

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserExplorePage
import com.example.kotlininstagramapp.Models.Story
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.model.HomePagePostItem
import com.example.kotlininstagramapp.ui.Story.StoryAdapter
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.TextHighlighter
import com.example.ns.ui.NSDynamicImageButton
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


class PostsAdapter(
    private var posts: ArrayList<HomePagePostItem>,
    private val fragment: HomeFragment,
    private val fragmentManager: FragmentManager,
    private val recyclerView: RecyclerView,
    private val databaseHelper: DatabaseHelper
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val defaultImage = R.drawable.profile
    private val handler = Handler(Looper.getMainLooper())
    private val VIEW_TYPE_HORIZONTAL_LIST = 1
    private val VIEW_TYPE_VERTICAL_ITEM = 2

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private val debounceTime = 200L
            private var lastScrollTime = 0L

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastScrollTime > debounceTime) {
                    lastScrollTime = currentTime
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                    layoutManager?.let {
                        val firstVisibleItemPosition = it.findFirstCompletelyVisibleItemPosition()
                        if (firstVisibleItemPosition != -1) {
                            handler.removeCallbacksAndMessages(null)
                            handler.post {
                                handleVideoPlayback(firstVisibleItemPosition)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun handleVideoPlayback(position: Int) {
        if (posts[position].content.contains("videos")) {
            (recyclerView.findViewHolderForAdapterPosition(position - 1) as? PostViewHolder)
                ?.post_vv_postvideo?.player?.pause()

            if(!(recyclerView.findViewHolderForAdapterPosition(position) as? PostViewHolder)?.isVideoPausedByHand!!){
                (recyclerView.findViewHolderForAdapterPosition(position) as? PostViewHolder)
                    ?.post_vv_postvideo?.player?.play()
            }


            (recyclerView.findViewHolderForAdapterPosition(position + 1) as? PostViewHolder)
                ?.post_vv_postvideo?.player?.pause()
        } else {
            (recyclerView.findViewHolderForAdapterPosition(position + 1) as? PostViewHolder)
                ?.post_vv_postvideo?.player?.pause()

            (recyclerView.findViewHolderForAdapterPosition(position - 1) as? PostViewHolder)
                ?.post_vv_postvideo?.player?.pause()
        }
    }

    fun addPosts(newPosts: List<HomePagePostItem>) {
        val previousSize = posts.size
        posts.addAll(newPosts)
        notifyItemRangeInserted(previousSize, newPosts.size)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HORIZONTAL_LIST else VIEW_TYPE_VERTICAL_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HORIZONTAL_LIST) {
            val view = inflater.inflate(R.layout.item_storieslist, parent, false)
            StoriesViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.card_post, parent, false)
            PostViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_HORIZONTAL_LIST -> {
                (holder as StoriesViewHolder).bind()
            }
            VIEW_TYPE_VERTICAL_ITEM -> {
                val userPostItem = posts[position]
                val viewHolder = holder as PostViewHolder
                with(viewHolder) {
                    fullNameTextView.text = userPostItem.userFullName
                    post_tvusername.text = userPostItem.userName
                    post_tvdescription.text = userPostItem.postDescription
                    TextHighlighter.highlightWordsTextView(post_tvdescription)
                    post_tv_dateago.text = getTimeAgo(userPostItem.creationDate.toLong())
                    post_tv_likecount.text = "${userPostItem.likeCount.toInt()} beğenme"

                    showComment.setOnClickListener {
                        val bottomSheetFragment = CommentBottomSheetFragment(
                            userPostItem.postId,
                            userPostItem.userId,
                            userPostItem.content
                        )
                        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
                    }

                    fullNameTextView.setOnClickListener {
                        val intent = Intent(fragment.context, UserExplorePage::class.java).apply {
                            putExtra("USER_ID", userPostItem.userId)
                        }
                        fragment.startActivity(intent)
                    }

                    setupLikeButton(this, userPostItem)

                    Glide.with(fragment)
                        .load(userPostItem.userProfileImage)
                        .placeholder(defaultImage)
                        .error(defaultImage)
                        .into(post_profileimage)

                    loadMedias(this, userPostItem)
                }
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    private fun loadMedias(holder: PostViewHolder, userPostItem: HomePagePostItem) {
        val contentType = if (userPostItem.content.contains("videos")) "video" else "image"

        if (contentType == "video") {
            holder.post_vv_postvideo.visibility = View.VISIBLE
            holder.post_iv_postimage.visibility = View.GONE

            val player = ExoPlayer.Builder(fragment.requireContext()).build()
            holder.post_vv_postvideo.player = player
            val uri = Uri.parse(userPostItem.content)
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.playWhenReady = true

            holder.speedTextView.setOnClickListener {
                val currentSpeed = holder.speedTextView.text.toString()
                val newSpeed = when (currentSpeed) {
                    "1x" -> 1.5f
                    "1.5x" -> 2f
                    else -> 1f
                }
                holder.speedTextView.text = "${newSpeed}x"
                player.playbackParameters = PlaybackParameters(newSpeed)
            }

            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        holder.iv_playPauseButton.setImageResource(R.drawable.pause)
                    } else {
                        holder.iv_playPauseButton.setImageResource(R.drawable.play)
                    }
                }
            })

            holder.iv_playPauseButton.setOnClickListener {
                if (player.isPlaying) {
                    player.pause()
                    holder.isVideoPausedByHand =true
                } else {
                    player.play()
                }
            }
        } else {
            holder.post_vv_postvideo.visibility = View.GONE
            holder.post_iv_postimage.visibility = View.VISIBLE
            Glide.with(fragment).load(userPostItem.content).into(holder.post_iv_postimage)
        }
    }

    private fun setupLikeButton(holder: PostViewHolder, userPostItem: HomePagePostItem) {
        var debounceJob: Job? = null
        CoroutineScope(Dispatchers.Main).launch {
            val isLiked = withContext(Dispatchers.IO) {
                databaseHelper.isPostLiked(userPostItem.postId.toInt())
            }
            holder.post_ivlike.setLiked(isLiked)
            holder.post_ivlike.onLikeStateChange { newLikeState ->
                val text = holder.post_tv_likecount.text
                when(newLikeState){
                    true -> holder.post_tv_likecount.text = (text.split(" ")[0].toInt()+1).toString()+" beğenme"
                    false -> holder.post_tv_likecount.text = (text.split(" ")[0].toInt()-1).toString()+" beğenme"
                }
                debounceJob?.cancel()
                debounceJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(3000)
                    if (newLikeState) {
                        databaseHelper.likePost(userPostItem.postId.toInt())
                        databaseHelper.addNotification(
                            userPostItem.userId,
                            "post_like",
                            userPostItem.content
                        )
                    } else {
                        databaseHelper.unlikePost(userPostItem.postId.toInt())
                    }
                }
            }
        }
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var isVideoPausedByHand = false
        val fullNameTextView: TextView = itemView.findViewById(R.id.post_tv_fullname)
        val post_tv_dateago: TextView = itemView.findViewById(R.id.post_tv_dateago)
        val post_profileimage: CircleImageView = itemView.findViewById(R.id.post_profileimage)
        val post_iv_postimage: ImageView = itemView.findViewById(R.id.post_iv_postimage)
        val post_vv_postvideo: PlayerView = itemView.findViewById(R.id.post_vv_postvideo)
        val post_tvusername: TextView = itemView.findViewById(R.id.post_tvusername)
        val post_tvdescription: TextView = itemView.findViewById(R.id.post_tvdescription)
        val showComment: TextView = itemView.findViewById(R.id.tv_showcomments)
        val post_ivlike: NSDynamicImageButton = itemView.findViewById(R.id.post_ivlike)
        val post_tv_likecount: TextView = itemView.findViewById(R.id.post_tv_likecount)
        val speedTextView: TextView = itemView.findViewById(R.id.tv_speed)
        val iv_playPauseButton: ImageView = itemView.findViewById(R.id.iv_playPauseButton)
    }

    inner class StoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val horizontalRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_homeFragmentStories)
        private val horizontalAdapter = StoryAdapter(fragment, listOf())

        init {
            horizontalRecyclerView.adapter = horizontalAdapter
            horizontalRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }

        fun bind() {
            CoroutineScope(Dispatchers.IO).launch {
               // val stories2: List<Story> = FirebaseHelper().getFollowedUsersStories()
                val stories: List<Story> = databaseHelper.getFollowedUsersStories()
                withContext(Dispatchers.Main) {
                    horizontalAdapter.setData(stories)
                    EventBus.getDefault().postSticky(EventBusDataEvents.SendStories(stories))
                }
            }
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
        val years = days / 365
        val months = days / 30

        return when {
            seconds < 60 -> "$seconds seconds ago"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            years >= 1 -> if (years == 1L) "1 year ago" else "$years years ago"
            months >= 1 -> if (months == 1L) "1 month ago" else "$months months ago"
            else -> "$days days ago"
        }
    }

    fun pauseAllVideos() {
        for (i in 0 until itemCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? PostViewHolder
            holder?.post_vv_postvideo?.player?.pause()
        }
    }

    // Method to stop or release all video players
    fun stopAllVideos() {
        for (i in 0 until itemCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? PostViewHolder
            holder?.post_vv_postvideo?.player?.release()
        }
    }
}
