package com.example.kotlininstagramapp.ui.Story

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentStoryReviewBinding
import com.example.kotlininstagramapp.ui.dialogs.NSCircleProgress
import com.example.kotlininstagramapp.utils.DatabaseHelper
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class StoryReviewFragment : Fragment() {

    private var _binding: FragmentStoryReviewBinding? = null
    private val binding get() = _binding!!
    lateinit var gelenDosya: File

    @Inject
    lateinit var databaseHelper: DatabaseHelper

    lateinit var nsdialog:NSCircleProgress

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoryReviewBinding.inflate(inflater, container, false)
        nsdialog = NSCircleProgress(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filePath = requireArguments().getString("FILE_PATH")
        if (filePath != null) {
            println("----------FILE_PATH----- >> $filePath")
            gelenDosya = File(filePath)
            Glide.with(this).load(gelenDosya).into(binding.ivStoryReview)
        }

        Glide.with(this).load(UserSingleton.userModel?.profilePicture).into(binding.ivProfileStoryPreview)


        binding.ivSendStory.setOnClickListener {
            nsdialog.showProgress()
            CoroutineScope(Dispatchers.IO).launch {
                val compressedImageFile = Compressor.compress(requireContext(), gelenDosya) {
                    quality(80)
                }

                databaseHelper.addStory(
                    compressedImageFile,
                    status = { Log.e("", "Status ____>>> $it") },
                    progress = {
                        if (it == 100) {
                            nsdialog.hideProgress()

                            findNavController().navigate(R.id.action_storyReviewFragment_to_homeFragment)
                        }
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
