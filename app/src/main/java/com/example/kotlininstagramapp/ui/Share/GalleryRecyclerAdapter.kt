package com.example.kotlininstagramapp.ui.Share

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlininstagramapp.R
import java.io.File
import java.util.concurrent.TimeUnit


class GalleryRecyclerAdapter(private val context: Context, private val mediaFiles: List<File>) : RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder>() {

    private var onItemClickListener: ((File) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_image_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mediaFiles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = mediaFiles[position]
        val isVideo = file.extension.equals("mp4", true)

        holder.bindData(file, isVideo)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(file)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoDurationText: TextView = itemView.findViewById(R.id.video_duration_text)
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        fun bindData(file: File, isVideo: Boolean) {
            if (isVideo) {
                Glide.with(context).load(getVideoThumbnail(file)).into(imageView)
                val videoDuration = getVideoDuration(file)
                videoDurationText.text = videoDuration
            } else {
                Glide.with(context)
                    .load(file)
                    .apply(RequestOptions().encodeQuality(50)) // Adjust the quality value as needed (0-100)
                    .into(imageView)
                videoDurationText.text = ""
            }
        }
    }

    fun setOnItemClickListener(listener: (File) -> Unit) {
        onItemClickListener = listener
    }




    private fun getVideoDuration(file: File): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        var duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?:0
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
        val minutes = seconds/60
        val remainingSeconds = seconds%60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }


    private fun getVideoThumbnail(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        return retriever.frameAtTime
    }
}
