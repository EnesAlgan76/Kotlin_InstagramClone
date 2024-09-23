package com.example.kotlininstagramapp.ui.Share

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import com.abedelazizshe.lightcompressorlibrary.config.StorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.VideoResizer
import com.abedelazizshe.lightcompressorlibrary.utils.saveVideoInExternal
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentShareNextBinding
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.example.kotlininstagramapp.utils.FullyCustomizedStorageConfiguration
import com.example.ns.ui.NSButton
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class ShareNextFragment : Fragment() {
    val shareProgressDialog = ShareProgressDialog()
    private val uris = mutableListOf<Uri>()
    @Inject
    lateinit var databaseHelper: DatabaseHelper
    lateinit var binding : FragmentShareNextBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_share_next, container, false)
        binding = FragmentShareNextBinding.bind(view)

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
                Glide.with(view.context).load(getVideoThumbnail(it)).into(binding.ivShareImage)
                uris.add(Uri.fromFile(gelenDosya))
            }else{
                //Glide.with(requireContext()).load(it).into(binding.ivShareImage)

                Picasso.get().load(it).memoryPolicy(MemoryPolicy.NO_CACHE).into(binding.ivShareImage)
            }

        }

        binding.ivBack.setOnClickListener {

        }





        binding.btnShare.setOnClickListener {
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
                            binding.etCaption.text.toString(),
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
                context = requireContext(), // => This is required
                uris = uris, // => Source can be provided as content uris
                isStreamable = false,
                storageConfiguration = FullyCustomizedStorageConfiguration(),

                configureWith = Configuration(
                    videoNames = listOf("compressed-video.mp4"),
                    quality = VideoQuality.LOW,
                    isMinBitrateCheckEnabled = true,
                  //  videoBitrateInMbps = 5,
                    disableAudio = false,
                    //resizer = VideoResizer.matchSize(360.0, 480.0) /*VideoResizer, ignore, or null*/
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

                    override fun onStart(index: Int) {}

                    override fun onSuccess(index: Int, size: Long, path: String?) {
                        CoroutineScope(Dispatchers.IO).launch {
                            databaseHelper.uploadPost(
                                Uri.fromFile(File(path)),
                                false,
                                binding.etCaption.text.toString(),

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
                        }
                    }

                    override fun onFailure(index: Int, failureMessage: String) {
                        Log.wtf("failureMessage", failureMessage)
                    }

                    override fun onCancelled(index: Int) {
                        Log.wtf("TAG", "compression has been cancelled")
                    }


                }
            )


        }
    }

    private fun getVideoThumbnail(file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        return retriever.frameAtTime
    }



}


