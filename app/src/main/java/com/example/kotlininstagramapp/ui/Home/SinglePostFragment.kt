package com.example.kotlininstagramapp.Home

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Home.PostsAdapter.PostViewHolder
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.Models.UserPostItem
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.model.HomePagePostItem
import com.example.kotlininstagramapp.databinding.FragmentSinglePostBinding
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.example.kotlininstagramapp.utils.TextHighlighter
import com.example.kotlininstagramapp.utils.getTimeAgo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SinglePostFragment : Fragment() {
    lateinit var binding: FragmentSinglePostBinding
    @Inject
    lateinit var databaseHelper: DatabaseHelper

    lateinit var speedTextView : TextView
    lateinit var iv_playPauseButton: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSinglePostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        speedTextView = view.findViewById(R.id.tv_speed)
        iv_playPauseButton = view.findViewById(R.id.iv_playPauseButton)

        val postId = arguments?.getInt("post_id")
        if (postId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                loadPostDetails(postId)
            }

        }
    }

    private suspend fun loadPostDetails(postId: Int) {

        val post = fetchPostById(postId)

        withContext(Dispatchers.Main){
            setupPostDetails(post)

        }


    }

    private fun setupPostDetails(post: HomePagePostItem) {
        binding.postTvFullname.text = post.userFullName
        binding.postTvusername.text = post.userName
        binding.postTvdescription.text = post.postDescription
        TextHighlighter.highlightWordsTextView(binding.postTvdescription)
        binding.postTvDateago.text = getTimeAgo(post.creationDate.toLong())
        binding.postTvLikecount.text = "${post.likeCount.toInt()} beğenme"

        binding.tvShowcomments.setOnClickListener {
            val bottomSheetFragment = CommentBottomSheetFragment(
                post.postId,
                post.userId,
                post.content
            )
            bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.postIvcomment.setOnClickListener {
            val bottomSheetFragment = CommentBottomSheetFragment(
                post.postId,
                post.userId,
                post.content
            )
            bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
        }



        binding.postTvFullname.setOnClickListener {
           // val bundle = Bundle().apply {
           //     putString("USER_ID", post.userId)
           // }
           // findNavController().navigate(R.id.userExplorePage,bundle)
        }

        setupLikeButton(post)

        Glide.with(requireContext())
            .load(post.userProfileImage)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .into(binding.postProfileimage)

        loadMedias(post)
    }

    private suspend fun fetchPostById(postId: Int): HomePagePostItem {
        return databaseHelper.getSinglePostById(postId)
    }

    private fun setupLikeButton( userPostItem: HomePagePostItem) {
        var debounceJob: Job? = null
        CoroutineScope(Dispatchers.Main).launch {
            val isLiked = withContext(Dispatchers.IO) {
                databaseHelper.isPostLiked(userPostItem.postId.toInt())
            }
            binding.postIvlike.setLiked(isLiked)
            binding.postIvlike.onLikeStateChange { newLikeState ->
                val text = binding.postTvLikecount.text
                when(newLikeState){
                    true -> binding.postTvLikecount.text = (text.split(" ")[0].toInt()+1).toString()+" beğenme"
                    false -> binding.postTvLikecount.text = (text.split(" ")[0].toInt()-1).toString()+" beğenme"
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


    private fun loadMedias(userPostItem: HomePagePostItem) {
        val contentType = if (userPostItem.content.contains("videos")) "video" else "image"

        if (contentType == "video") {
            binding.postVvPostvideo.visibility = View.VISIBLE
            binding.postIvPostimage.visibility = View.GONE

            val player = ExoPlayer.Builder(requireContext()).build()
            binding.postVvPostvideo.player = player
            val uri = Uri.parse(userPostItem.content)
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.playWhenReady = true

            speedTextView.setOnClickListener {
                val currentSpeed = speedTextView.text.toString()
                val newSpeed = when (currentSpeed) {
                    "1x" -> 1.5f
                    "1.5x" -> 2f
                    else -> 1f
                }
                speedTextView.text = "${newSpeed}x"
                player.playbackParameters = PlaybackParameters(newSpeed)
            }

            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        iv_playPauseButton.setImageResource(R.drawable.pause)
                    } else {
                        iv_playPauseButton.setImageResource(R.drawable.play)
                    }
                }
            })

            iv_playPauseButton.setOnClickListener {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }
        } else {
            binding.postVvPostvideo.visibility = View.GONE
            binding.postIvPostimage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(userPostItem.content).into(binding.postIvPostimage)
        }
    }
}