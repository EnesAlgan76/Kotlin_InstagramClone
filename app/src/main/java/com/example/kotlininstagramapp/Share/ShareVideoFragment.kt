package com.example.kotlininstagramapp.Share

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.iammert.library.cameravideobuttonlib.CameraVideoButton
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Mode
import org.greenrobot.eventbus.EventBus
import java.io.File


class ShareVideoFragment : Fragment() {
    lateinit var cameraView: CameraView
    lateinit var closeIcon: ImageView
    lateinit var videoRecordButton : CameraVideoButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_share_video, container, false)

        closeIcon = view.findViewById(R.id.iv_closeButtonVideo)
        closeIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        cameraView = view.findViewById(R.id.cameraViewShareVideo )
        videoRecordButton = view.findViewById(R.id.btn_record)

        videoRecordButton.setVideoDuration(10000)
        videoRecordButton.enableVideoRecording(true)
        videoRecordButton.enablePhotoTaking(false)

        cameraView.setLifecycleOwner(viewLifecycleOwner)
        cameraView.mode = Mode.VIDEO

        var fileName:String = System.currentTimeMillis().toString()+".mp4"
        val fileToUpload = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), fileName)

        videoRecordButton.actionListener = object : CameraVideoButton.ActionListener{
            override fun onStartRecord() {
                Log.v("TEST", "Start recording video")
                cameraView.takeVideo(fileToUpload)
            }

            override fun onEndRecord() {
                cameraView.stopVideo()
                Log.v("TEST", "Stop recording video")
            }

            override fun onDurationTooShortError() {
                Log.v("TEST", "Toast or notify user")
            }

            override fun onSingleTap() {
                Log.v("TEST", "Take photo here")
            }
        }


//        captureButton.setOnTouchListener(object  :View.OnTouchListener{
//            override fun onTouch(p0: View?, motion: MotionEvent?): Boolean {
//                if(motion!!.action ==MotionEvent.ACTION_DOWN){
//                    cameraView.takeVideo(fileToUpload)
//                    return true
//                }
//                else if(motion.action ==MotionEvent.ACTION_UP){
//                    cameraView.stopVideo()
//                    return true
//                }
//                return false
//            }
//
//        })

        cameraView.addCameraListener(object  : CameraListener(){
            override fun onVideoRecordingStart() {
                println("*** \"Video Kaydediliyor\" ***")
                super.onVideoRecordingStart()
            }

            override fun onVideoTaken(result: VideoResult) {
                println("*** \"Video Kaydedildi onVideoTaken \" ***")
                goShareNextFragmet(result.file)
                super.onVideoTaken(result)
            }

        })


        return view
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


    override fun onResume() {
        super.onResume()
        Log.e("------------","video view RESUME")
        cameraView.open()
    }

    override fun onStop() {
        cameraView.close()
        Log.e("------------","video view onStop")
        super.onStop()
    }

    override fun onPause() {
        Log.e("------------","video view PAUSE")
        cameraView.close()
        super.onPause()
    }

    override fun onDestroy() {
        cameraView.destroy()
        Log.e("------------","video view onDestroy")
        super.onDestroy()
    }



}