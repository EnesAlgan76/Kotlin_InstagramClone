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
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firestore.v1.DocumentTransform.FieldTransform.ServerValue
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
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
    val imageRef = storageReference.child("posts/${mAuth.currentUser?.uid}/${postId}")
    val shareProgressDialog = ShareProgressDialog()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_share_next, container, false)
        image = view.findViewById(R.id.iv_share_next)
        tvShare = view.findViewById(R.id.tv_paylas)
        explanation = view.findViewById(R.id.et_explanation)
        ivBack = view.findViewById<ImageView>(R.id.iv_back)
        val db = FirebaseFirestore.getInstance()

        gelenDosya?.let {
            if(gelenDosya!!.extension=="mp4"){
               // Picasso.get().load(getVideoThumbnail(it)).into(image)
                Glide.with(view.context).load(getVideoThumbnail(it)).into(image)
            }else{
                Picasso.get().load(it).into(image)
            }

        }

        ivBack.setOnClickListener {

        }





        tvShare.setOnClickListener {
            shareProgressDialog.show(requireActivity().supportFragmentManager, "ShareProgressDialog")
            shareProgressDialog.isCancelable = false

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    if (gelenDosya!!.extension == "mp4") {
                        val input = gelenDosya!!
                        val output: File
                        val outputFileName = System.currentTimeMillis().toString()+".mp4"
                        val outputDirectory = input.parentFile
                        output = File(outputDirectory, outputFileName)
                      //  val command2 =  "-y -i ${input.absolutePath} -preset medium -crf 23 -vf format=yuv420p -c:v h264 ${output.absolutePath}"
                      //  val command3 = "-i ${input.absolutePath} -c:v h264 -crf 18 -preset slow -c:a aac -b:a 192k -vf scale=1280:-2 ${output.absolutePath}"
                        val command4 = "-i ${input.absolutePath} -c:v h264 -crf 23 -preset medium ${output.absolutePath}"

                        try {
                            FFmpegKit.execute(command4)
                        }catch (e:Throwable){
                            Log.e("SIKIŞTIRMA hATASI", e.message.toString())
                        }

                        uploadImageToStorage2(Uri.fromFile(output))


                    }
                    else{
                        val compressedImageFile = Compressor.compress(requireContext(), gelenDosya!!) { quality(80) }
                        val compressedImageUri = Uri.fromFile(compressedImageFile)
                        uploadImageToStorage2(compressedImageUri)
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



    private suspend fun uploadImageToStorage2(compressedImageUri: Uri) {
        try {
            val uploadTask = imageRef.putFile(compressedImageUri)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()

                Log.e("","Progress ____>>> "+progress)
                shareProgressDialog.tvProgress.text = "Yükleniyor : %${progress}"
            }

            uploadTask.addOnSuccessListener {
                    GlobalScope.launch {
                        val url = imageRef.downloadUrl.await().toString()
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