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
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*

class ShareNextFragment : Fragment() {
    var gelenDosya: File? =null;
    lateinit var image:ImageView
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
        val db = FirebaseFirestore.getInstance()
        val imageUri = Uri.parse("file://${gelenDosya?.absolutePath}")

        gelenDosya?.let {
            if(gelenDosya!!.extension=="mp4"){
               // Picasso.get().load(getVideoThumbnail(it)).into(image)
                Glide.with(view.context).load(getVideoThumbnail(it)).into(image)
            }else{
                Picasso.get().load(it).into(image)
            }

        }

        tvShare.setOnClickListener {
            var compressedImageUri: Uri
            shareProgressDialog.show(requireActivity().supportFragmentManager, "ShareProgressDialog")
            shareProgressDialog.isCancelable = false

            GlobalScope.launch(Dispatchers.IO) {
                val compressedImageFile = Compressor.compress(requireContext(), gelenDosya!!) { quality(80) }
                compressedImageUri = Uri.parse("file://${compressedImageFile.absolutePath}")
                withContext(Dispatchers.Main) {
                    uploadImageToStorage(compressedImageUri)
                }
            }
        }


        Log.e(".","ShareNextFragment ÇALIŞTI")
        return view
    }



     fun uploadImageToStorage(compressedImageUri: Uri) {
        val uploadTask = imageRef.putFile(compressedImageUri)

         uploadTask.addOnProgressListener { taskSnapshot ->
             val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()

             Log.e("","Progress ____>>> "+progress)
             shareProgressDialog.tvProgress.text = "Yükleniyor : %${progress}"
         }

        uploadTask.addOnSuccessListener { task->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val url = uri.toString()
                var post = Post(mAuth.currentUser!!.uid,postId,FieldValue.serverTimestamp().toString(),explanation.text.toString(),url)
                uploadPosttoFirestore(post)
                println(url)
            }
        }.addOnFailureListener { exception ->
            // Handle any errors that occurred during the upload
            println("Upload failed: $exception")
            shareProgressDialog.dismiss()
        }
    }

    private fun uploadPosttoFirestore(post: Post) {
        var userDocRef = firestore.collection("userPosts").document(post.userId)
        val postMap = hashMapOf(
            "date" to post.date,
            "explanation" to post.explanation,
            "url" to post.url,
        )
       // userDocRef.collection("posts").add(hashMapOf(post.postId to postMap))
        userDocRef.set(hashMapOf("userId" to post.userId)).addOnSuccessListener {
            userDocRef.collection("posts").document(post.postId).set(postMap)
                .addOnSuccessListener { documentReference ->
                    println("Post added with ID: ${documentReference}")
                    shareProgressDialog.dismiss()
                }
                .addOnFailureListener { e ->
                    println("Error adding post: $e")
                    shareProgressDialog.dismiss()
                }
        } .addOnFailureListener { e ->
            println("Error setting user data: $e")
            shareProgressDialog.dismiss()
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
        // Do something with the received message
    }

    private fun getVideoThumbnail(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        return retriever.frameAtTime
    }



}