package com.example.kotlininstagramapp.Home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.Story.StoryReviewActivity
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Facing
import org.greenrobot.eventbus.EventBus
import java.io.File

class CameraFragment : Fragment() {
    lateinit var cameraView: CameraView
    lateinit var switchCameraButton: ImageView
    lateinit var capture: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_camera,container,false)

        cameraView = view.findViewById(R.id.cameraview)
        switchCameraButton = view.findViewById(R.id.iv_switchfrontcamera)
        capture = view.findViewById(R.id.iv_capturecamera)

        switchCameraButton.setOnClickListener {
            if(cameraView.facing ==Facing.BACK){
                cameraView.facing = Facing.FRONT
            }else{
                cameraView.facing = Facing.BACK
            }
        }

        capture.setOnClickListener {
            if(cameraView.facing ==Facing.BACK){
                cameraView.takePicture()
            }else{
                cameraView.takePictureSnapshot()
            }

        }
        cameraView.setLifecycleOwner(viewLifecycleOwner)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        cameraView.addCameraListener(object  : CameraListener(){
            override fun onPictureTaken(result: PictureResult) {
                // Convert the image to a byte array
                //val file = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/", "filename.jpg")

                val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "${System.currentTimeMillis().toString()}.jpg")


                try {
                    file.createNewFile()
                    result.toFile(file, object : FileCallback {
                        override fun onFileReady(file: File?) {
                            file?.let {

                                val intent = Intent(activity, StoryReviewActivity::class.java)
                                intent.putExtra("FILE_PATH", file.absolutePath)
                                startActivity(intent)

                            } ?: run {
                                Log.e("*********", "File is null")
                            }
                        }

                    })
                } catch (e: Exception) {
                    Log.e("*********", "Error creating file: ${e.message}")
                }
                super.onPictureTaken(result)
            }

        })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        cameraView.open()
    }

    override fun onPause() {
        cameraView.close()
        super.onPause()
    }

    override fun onStop() {
        cameraView.close()
        super.onStop()
    }

    override fun onDestroy() {
        cameraView.destroy()
        super.onDestroy()
    }



}