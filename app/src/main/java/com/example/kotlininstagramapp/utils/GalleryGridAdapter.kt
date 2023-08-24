import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.File
import java.util.concurrent.TimeUnit

class GalleryGridAdapter(context: Context, private val mediaFiles: List<File>) :
    ArrayAdapter<File>(context, R.layout.grid_image_view, mediaFiles) {

    private class ViewHolder(
        val videoDuration: TextView,
        val imageView: ImageView
        )



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View




        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_image_view, parent, false)
            val imageView: ImageView = view.findViewById(R.id.image_view)
            var videoDurationText:TextView = view.findViewById(R.id.video_duration_text)
            viewHolder = ViewHolder(videoDurationText, imageView)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }


        val file = mediaFiles[position]
        val isVideo = file.extension.equals("mp4", true)

        val videoDurationText  =viewHolder.videoDuration
        val imageView = viewHolder.imageView

        if(isVideo){
            Glide.with(context).load(getVideoThumbnail(file)).into(imageView)
            val videoDuration = getVideoDuration(file)
            videoDurationText.text = videoDuration
        }else{
            Glide.with(context).load(mediaFiles[position]).into(imageView)

        }

//        if (file.extension.lowercase() == "mp4") {
//            println("************** >>>> ${file.absolutePath}")
//            val videoThumbnail = getVideoThumbnail2(file)
//            imageView.setImageBitmap(videoThumbnail)
//            val videoDuration = getVideoDuration(file)
//            videoDurationText.text = videoDuration
//           // videoDurationText.visibility = View.VISIBLE
//        }
//
//        Picasso.get().load(mediaFiles[position]).resize(250,250).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageView)

        return view
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

    private fun getVideoThumbnail2(videoFile: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFile.absolutePath)

        val timeInMicroseconds = 1000000L // 1 second
        return retriever.getFrameAtTime(timeInMicroseconds, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    }

    private fun getVideoThumbnail(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        return retriever.frameAtTime
    }


}
