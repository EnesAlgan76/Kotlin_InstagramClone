package com.example.kotlininstagramapp.ui.Share

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentTrimVideoBinding
import com.example.kotlininstagramapp.ui.dialogs.NSCircleProgress
import com.example.ns.video_trimmer.event.OnVideoEditedEvent
import java.io.File

class TrimVideoFragment : Fragment(), OnVideoEditedEvent {

    lateinit var binding: FragmentTrimVideoBinding
    lateinit var nsdialog:NSCircleProgress

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrimVideoBinding.inflate(layoutInflater)
        nsdialog = NSCircleProgress(requireContext())
        val uriString = arguments?.getString("uri")
        val uri: Uri? = uriString?.let { Uri.parse(it) }

        if (uri != null) {
            val destinationFile = File(requireContext().cacheDir, "trimmed_video.mp4")
            binding.videoTrimmer.apply {
                setVideoBackgroundColor(resources.getColor(R.color.white))
                setOnTrimVideoListener(this@TrimVideoFragment)
                setVideoURI(uri)
                setDestinationPath(destinationFile.path)
                setVideoInformationVisibility(true)
                setMaxDuration(30)
                setMinDuration(0)
            }
        }

        binding.ivSave.setOnClickListener {
            nsdialog.showProgress()
            binding.videoTrimmer.saveVideo()
        }



        return binding.root
    }



    override fun getResult(uri: Uri) {
        val bundle = Bundle().apply {
            putString("uri", uri.toString())
        }
        nsdialog.hideProgress()
        binding.videoTrimmer.onCancelClicked()
        findNavController().navigate(R.id.shareNextFragment, bundle)
    }


    override fun onError(message: String) {
        println("Save video error :$message")
    }

    override fun onProgress(percentage: Int) {

    }
}
