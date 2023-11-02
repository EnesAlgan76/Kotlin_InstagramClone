package com.example.kotlininstagramapp.Share

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.google.firebase.storage.FirebaseStorage
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*


class ShareCameraFragment : Fragment() {
    lateinit var cameraView: CameraView
    lateinit var captureButton : View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_share_camera, container, false)

        cameraView = view.findViewById(R.id.camraView)
        captureButton = view.findViewById(R.id.iv_capture)
        cameraView.setLifecycleOwner(viewLifecycleOwner)

        cameraView.mapGesture(Gesture.PINCH,GestureAction.ZOOM)
        cameraView.mapGesture(Gesture.TAP,GestureAction.AUTO_FOCUS)
        cameraView.mode= Mode.PICTURE

        captureButton.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                cameraView.takePicture()
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        cameraView.addCameraListener(object  : CameraListener(){
            override fun onPictureTaken(result: PictureResult) {
                // Convert the image to a byte array
                val file = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/", "filename.jpg")
                result.toFile(file,object: FileCallback{
                    override fun onFileReady(file: File?) {
                        goShareNextFragmet(file)
                    }

                })
                super.onPictureTaken(result)
            }

        })
        super.onViewCreated(view, savedInstanceState)
    }

    private fun goShareNextFragmet(file: File?) {
        val flShareNextFrame = requireActivity().findViewById<FrameLayout>(R.id.fl_shareNextFrame)
        val mainLayout = requireActivity().findViewById<ConstraintLayout>(R.id.mainLayout)

        mainLayout.visibility = View.GONE
        flShareNextFrame.visibility = View.VISIBLE

        EventBus.getDefault().postSticky(file?.let {
            EventBusDataEvents.SendMediaFile(it)
        })

        val shareNextFragment = ShareNextFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fl_shareNextFrame, shareNextFragment)
            .addToBackStack("ShareNextFragment")
            .commit()
    }

    suspend fun uploadFileToStorage(file: File?) {
        val storage = FirebaseStorage.getInstance()

        // Create a reference to the storage location where you want to save the image
        val storageRef = storage.getReference("images/${UUID.randomUUID()}.jpg")

        try {
            storageRef.putFile(Uri.fromFile(file)).await()
            Log.e("------------","Upload is Succesful")
        }catch (e: Throwable){
            Log.e("------------","Error Uploading : ${e.message}")
        }


    }




    override fun onResume() {
        super.onResume()
        Log.e("------------","camera view RESUME")
        cameraView.open()
    }

    override fun onPause() {
        Log.e("------------","camera view PAUSE")
        cameraView.close()
        super.onPause()
    }

    override fun onStop() {
        cameraView.close()
        Log.e("------------","camera view onStop")
        super.onStop()
    }

    override fun onDestroy() {
        cameraView.destroy()
        Log.e("------------","camera view onDestroy")
        super.onDestroy()
    }


}