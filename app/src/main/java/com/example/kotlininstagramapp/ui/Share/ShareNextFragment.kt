package com.example.kotlininstagramapp.ui.Share

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
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.DatabaseHelper
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class ShareNextFragment : Fragment() {
    var gelenDosya: File? =null
    lateinit var image:ImageView
    lateinit var ivBack:ImageView
    lateinit var tvShare :TextView
    lateinit var explanation :TextView
    val shareProgressDialog = ShareProgressDialog()
    private val uris = mutableListOf<Uri>()
    @Inject
    lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_share_next, container, false)
        image = view.findViewById(R.id.iv_share_next)
        tvShare = view.findViewById(R.id.tv_paylas)
        explanation = view.findViewById(R.id.et_explanation)
        ivBack = view.findViewById(R.id.iv_back)

        val uriString = arguments?.getString("uri")
        val uri: Uri? = uriString?.let { Uri.parse(it) }

        val gelenDosya = if (uri != null) {
            if (uri.scheme == "file") {
                uri.toFile()
            } else {
                File(uri.path)
            }
        }else{
            null
        }


        gelenDosya?.let {
            if(gelenDosya.extension=="mp4"){
                Glide.with(view.context).load(getVideoThumbnail(it)).into(image)
                uris.add(Uri.fromFile(gelenDosya))
            }else{
                Glide.with(view.context).load(it).into(image)
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
                    }
                    else{
                        val compressedImageFile = Compressor.compress(requireContext(), gelenDosya!!) { quality(80) }
                        val compressedImageUri = Uri.fromFile(compressedImageFile)
                        databaseHelper.uploadPost(
                            compressedImageUri,
                            true,
                            explanation.text.toString(),
                            onProgress = {
                                Log.e("","Progress ____>>> "+it)
                                shareProgressDialog.tvProgress.text = "Yükleniyor : %${it}"
                            },
                            onSuccess = {
                                shareProgressDialog.dismiss()
                                navigateHomeFragment()
                                UserSingleton.userModel!!.postCount +=1
                                Log.e("SUCCESS",it)
                            },

                            onFailed = {
                                Log.e("FAİL",it)
                            }
                        )


                       // uploadImageToStorage2(compressedImageUri, image =true)
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

    private fun navigateHomeFragment() {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_left)
            .setExitAnim(0)
            .build()

        findNavController().navigate(R.id.homeFragment, null, navOptions)
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

                            databaseHelper.uploadPost(
                                Uri.fromFile(File(path)),
                                false,
                                explanation.text.toString(),

                                onProgress = {
                                    Log.e("","Progress ____>>> "+it)
                                    shareProgressDialog.tvProgress.text = "Yükleniyor : %${it}"
                                },
                                onSuccess = {
                                    shareProgressDialog.dismiss()
                                    navigateHomeFragment()

                                    UserSingleton.userModel!!.postCount +=1
                                    Log.e("SUCCESS",it)
                                },

                                onFailed = {
                                    Log.e("FAİL",it)
                                }
                            )
                           // uploadImageToStorage2(Uri.fromFile(File(path)), image =false)
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

    private fun getVideoThumbnail(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        return retriever.frameAtTime
    }



}