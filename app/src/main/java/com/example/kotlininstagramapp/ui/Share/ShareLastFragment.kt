package com.example.kotlininstagramapp.ui.Share

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentShareLastBinding

class ShareLastFragment : Fragment() {

    lateinit var binding: FragmentShareLastBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShareLastBinding.inflate(layoutInflater)

        val uriString = arguments?.getString("uri")
        val uri: Uri? = uriString?.let { Uri.parse(it) }


        binding.ivCropResult.setImageURI(uri)


        return binding.root
    }
}
