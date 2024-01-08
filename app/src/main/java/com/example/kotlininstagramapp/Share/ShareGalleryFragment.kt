package com.example.kotlininstagramapp.Share

import GalleryGridAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Login.RegisterFragment
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityShareBinding
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.FileOperations
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class ShareGalleryFragment : Fragment() {

    lateinit var spinner :Spinner
    lateinit var recyclerView: RecyclerView
    lateinit var bigImage:ImageView
    lateinit var closeButton:ImageView
    lateinit var bigVideo:VideoView
    lateinit var buttonIleri:TextView
    private var selectedMedia: File? = null

    val REQUEST_EXTERNAL_STORAGE_PERMISSION = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        var view = inflater.inflate(R.layout.fragment_share_gallery, container, false)

        spinner = view.findViewById(R.id.spn_directory_name)
        recyclerView = view.findViewById(R.id.recyclerView)
        bigImage = view.findViewById(R.id.iv_gallery)
        bigVideo = view.findViewById(R.id.vv_gallery)
        closeButton = view.findViewById(R.id.iv_closeButton)
        buttonIleri = view.findViewById(R.id.tv_ileri)
        closeButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        handleIleriButton()
        initializeSpinner()

        Log.e(".","ShareGalleryFragment ÇALIŞTI")
        return view
    }



    private fun handleIleriButton() {
        val flShareNextFrame = requireActivity().findViewById<FrameLayout>(R.id.fl_shareNextFrame)
        val mainLayout = requireActivity().findViewById<ConstraintLayout>(R.id.mainLayout)

        buttonIleri.setOnClickListener {
            println("**********TIKLANDI**************")
            bigVideo.stopPlayback()
            mainLayout.visibility = View.GONE
            flShareNextFrame.visibility = View.VISIBLE

            EventBus.getDefault().postSticky(EventBusDataEvents.SendMediaFile(selectedMedia!!))

            val shareNextFragment = ShareNextFragment()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_shareNextFrame, shareNextFragment)
                .addToBackStack("ShareNextFragment")
                .commit()
        }
    }



    private fun initializeSpinner() {
        var spinnerOptions = arrayOf("DCIM","Pictures","Download","Movies")
        var adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, spinnerOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFolderName = spinnerOptions[position]

                GlobalScope.launch(Dispatchers.Main) {
                    val resimYolListesi = withContext(Dispatchers.Default) {
                        FileOperations.listImageFiles(selectedFolderName)
                    }
                    val result = resimYolListesi
                    var fileList = result!!.map { File(it) }

                    val adapter = GalleryRecyclerAdapter(requireActivity(),fileList)
                    val gridLayoutManager = GridLayoutManager(requireContext(), 3)
                    recyclerView.layoutManager = gridLayoutManager
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener { file ->
                        selectedMedia = file

                        if (file.extension.equals("mp4", true)) {
                            bigImage.visibility = View.GONE
                            bigVideo.visibility = View.VISIBLE
                            bigVideo.setVideoURI(file.toUri())
                            bigVideo.start()
                        } else {
                            bigVideo.stopPlayback()
                            bigImage.visibility = View.VISIBLE
                            bigVideo.visibility = View.GONE

                            Glide.with(requireActivity())
                                .load(file)
                                .override(1080, 1920)
                                .into(bigImage)
                        }
                    }


                    /*
                    val gridAdapter = GalleryGridAdapter(requireActivity(), fileList)
                    gridview.adapter = gridAdapter
                    println(result.toString())
                    if (fileList.isNotEmpty()){
                        selectedMedia =fileList[0]
                    }


                    gridview.setOnItemClickListener { adapterView, view, position, l ->
                        val file = fileList[position]
                        selectedMedia = file

                        if(file.extension.equals("mp4",true)){

                            bigImage.visibility=View.GONE
                            bigVideo.visibility = View.VISIBLE
                            bigVideo.setVideoURI(file.toUri())
                            bigVideo.start()
                        }else{
                            bigVideo.stopPlayback()
                            bigImage.visibility=View.VISIBLE
                            bigVideo.visibility = View.GONE

                            Glide.with(requireActivity())
                                .load(file)
                                .override(1080  , 1920) // Set target width and height
                                .into(bigImage)


                        }
                    }*/


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