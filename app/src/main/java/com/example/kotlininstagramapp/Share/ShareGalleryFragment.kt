package com.example.kotlininstagramapp.Share

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.kotlininstagramapp.R

class ShareGalleryFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_share_gallery, container, false)

        var spinner = view.findViewById<Spinner>(R.id.spn_directory_name)
        var spinnerOptions = arrayOf("Galeri","Kamera","İndirilenler")
        var adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, spinnerOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        return view
    }

}