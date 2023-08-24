package com.example.kotlininstagramapp.Share

import GalleryGridAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.Spinner
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.FileOperations
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.io.File

class ShareGalleryFragment : Fragment() {

    lateinit var spinner :Spinner
    lateinit var gridview :GridView
    lateinit var bigImage:ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        var view = inflater.inflate(R.layout.fragment_share_gallery, container, false)

        spinner = view.findViewById(R.id.spn_directory_name)
        gridview = view.findViewById(R.id.grid_view_gallery)
        bigImage = view.findViewById<ImageView>(R.id.iv_gallery);
        initializeSpinner()
        //listOf("DCIM","Download","Pictures","WhatsApp/Media/WhatsApp Images")
//        GlobalScope.launch {
//            var resimYolListesi = async {
//                FileOperations.listImageFiles("Pictures")
//            }
//
//            val result = resimYolListesi.await()
//
//            println(result.toString())
//        }


        return view
    }
    
    private fun initializeSpinner2 (){
        var spinerOptions = arrayOf("WhatsAppImages")

    }


    private fun initializeSpinner() {
        var spinnerOptions = arrayOf("DCIM","Pictures")
        var adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, spinnerOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFolderName = spinnerOptions[position]
               // var fileList = listOf<File>()
                GlobalScope.launch(Dispatchers.Main) {
                    val resimYolListesi = withContext(Dispatchers.Default) {
                        FileOperations.listImageFiles(selectedFolderName)
                    }
                    val result = resimYolListesi
                    var fileList = result!!.map { File(it) }
                    val gridAdapter = GalleryGridAdapter(requireActivity(), fileList)
                    gridview.adapter = gridAdapter
                    println(result.toString())


                    gridview.setOnItemClickListener { adapterView, view, position, l ->
                        Picasso.get().load(fileList.get(position)).into(bigImage)
                    }


                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("Not yet implemented")
            }

        }
    }

}