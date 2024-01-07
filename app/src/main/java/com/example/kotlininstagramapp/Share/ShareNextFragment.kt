package com.example.kotlininstagramapp.Share

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.arthenica.ffmpegkit.FFmpegKit
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class ShareNextFragment : Fragment() {
    var gelenDosya: File? =null
    lateinit var image:ImageView
    lateinit var ivBack:ImageView
    lateinit var tvShare :TextView
    lateinit var explanation :TextView
    var mAuth = FirebaseAuth.getInstance()
    var firestore = FirebaseFirestore.getInstance()
    //lateinit var storageReference: StorageReference
    val postId = UUID.randomUUID().toString()
    var  storageReference = FirebaseStorage.getInstance().reference
    val imageRef = storageReference.child("posts/${mAuth.currentUser?.uid}/images/${postId}")
    val videoRef = storageReference.child("posts/${mAuth.currentUser?.uid}/videos/${postId}")
    val shareProgressDialog = ShareProgressDialog()

    private val uris = mutableListOf<Uri>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_share_next, container, false)
        image = view.findViewById(R.id.iv_share_next)
        tvShare = view.findViewById(R.id.tv_paylas)
        explanation = view.findViewById(R.id.et_explanation)
        ivBack = view.findViewById(R.id.iv_back)

        gelenDosya?.let {
            if(gelenDosya!!.extension=="mp4"){
               // Picasso.get().load(getVideoThumbnail(it)).into(image)
                Glide.with(view.context).load(getVideoThumbnail(it)).into(image)
                uris.add(Uri.fromFile(gelenDosya))
            }else{
                Picasso.get().load(it).into(image)
            }

        }

        ivBack.setOnClickListener {

        }





        tvShare.setOnClickListener {
            shareProgressDialog.show(requireActivity().supportFragmentManager, "ShareProgressDialog")
            shareProgressDialog.isCancelable = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (gelenDosya!!.extension == "mp4") {

                        processVideo()


//                        val input = gelenDosya!!
//                        val output: File
//                        val outputFileName = System.currentTimeMillis().toString()+".mp4"
//                        val outputDirectory = input.parentFile
//                        output = File(outputDirectory, outputFileName)
//                        val command4 = "-i ${input.absolutePath} -c:v h264 -crf 23 -preset medium ${output.absolutePath}"
//                        try {
//                            FFmpegKit.execute(command4)
//                        }catch (e:Throwable){
//                            Log.e("SIKIŞTIRMA hATASI", e.message.toString())
//                        }

                      //  uploadImageToStorage2(Uri.fromFile(output), image =false)


                    }
                    else{
                        val compressedImageFile = Compressor.compress(requireContext(), gelenDosya!!) { quality(80) }
                        val compressedImageUri = Uri.fromFile(compressedImageFile)
                        uploadImageToStorage2(compressedImageUri, image =true)
                    }

                } catch (e: Exception) {
                    // Handle any exceptions
                    e.printStackTrace()
                } finally {

                }
            }
        }

        return view
    }

    private fun processVideo() {

        CoroutineScope(Dispatchers.IO).launch {
            VideoCompressor.start(
                context = requireContext(),
                uris,
                isStreamable = false,
                sharedStorageConfiguration = SharedStorageConfiguration(
                    saveAt = SaveLocation.movies,
                    subFolderName = "nstavideos"
                ),
//                appSpecificStorageConfiguration = AppSpecificStorageConfiguration(
//
//                ),
                configureWith = Configuration(
                    quality = VideoQuality.LOW,
                    videoNames = uris.map { uri -> uri.pathSegments.last() },
                    isMinBitrateCheckEnabled = false,
                ),
                listener = object : CompressionListener {
                    override fun onProgress(index: Int, percent: Float) {
                        val roundedPercent = percent.toInt() // Convert percent to integer

                        if (roundedPercent < 100 && (roundedPercent % 5) == 0) {
                            CoroutineScope(Dispatchers.Main).launch {
                                shareProgressDialog.tvProgress.text = "Sıkıştırılıyor: %$roundedPercent"
                            }
                        }
                    }

                    override fun onStart(index: Int) {
                        println("------------------ >>>>>>>>>>>>>>>> "+uris.first())

                    }

                    override fun onSuccess(index: Int, size: Long, path: String?) {
                        println("BAŞARILI ---------- > ${path}")
                        CoroutineScope(Dispatchers.IO).launch {
                            uploadImageToStorage2(Uri.fromFile(File(path)), image =false)
                        }
                    }

                    override fun onFailure(index: Int, failureMessage: String) {
                        Log.wtf("failureMessage", failureMessage)
                    }

                    override fun onCancelled(index: Int) {
                        Log.wtf("TAG", "compression has been cancelled")
                    }
                },
            )
        }
    }


    private suspend fun uploadImageToStorage2(compressedMediaUri: Uri, image: Boolean) {
        try {
            var mediaRef :StorageReference
            if (image){
                mediaRef = imageRef
            }else{
                mediaRef = videoRef
            }

            val uploadTask = mediaRef.putFile(compressedMediaUri)


            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()

                Log.e("","Progress ____>>> "+progress)
                shareProgressDialog.tvProgress.text = "Yükleniyor : %${progress}"
            }

            uploadTask.addOnSuccessListener {
                    GlobalScope.launch {
                        val url = mediaRef.downloadUrl.await().toString()
                        val post = Post(mAuth.currentUser!!.uid, postId, System.currentTimeMillis().toString(), explanation.text.toString(), url)
                        uploadPostToFirestore2(post)
                    }
            }


        } catch (e: Exception) {
            shareProgressDialog.dismiss()
            e.printStackTrace()
        }
    }



    private suspend fun uploadPostToFirestore2(post: Post) {
        try {
            val userDocRef = firestore.collection("userPosts").document(post.userId)

            val postMap = hashMapOf(
                "date" to post.date,
                "explanation" to post.explanation,
                "url" to post.url,
                "likeCount" to "0"
            )

            userDocRef.set(hashMapOf("userId" to post.userId)).await()
            userDocRef.collection("posts").document(post.postId).set(postMap).await()
            shareProgressDialog.dismiss()
        } catch (e: Exception) {
            // Handle any exceptions
            e.printStackTrace()
        }
    }





    override fun onAttach(context: Context) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }


    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }

    @Subscribe(sticky = true)
    fun onMessageEvent(event: EventBusDataEvents.SendMediaFile) {
        gelenDosya = event.mediaFile
    }

    private fun getVideoThumbnail(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        return retriever.frameAtTime
    }



}