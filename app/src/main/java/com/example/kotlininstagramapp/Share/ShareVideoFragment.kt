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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Mode
import org.greenrobot.eventbus.EventBus
import java.io.File


class ShareVideoFragment : Fragment() {
    lateinit var cameraView: CameraView
    lateinit var captureButton : View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_share_video, container, false)

        cameraView = view.findViewById(R.id.cameraView )
        captureButton = view.findViewById(R.id.iv_record)
        cameraView.setLifecycleOwner(viewLifecycleOwner)
        cameraView.mode = Mode.VIDEO

        var fileName:String = System.currentTimeMillis().toString()+".mp4"
        var fileToUpload = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/",fileName)
        captureButton.setOnTouchListener(object  :View.OnTouchListener{
            override fun onTouch(p0: View?, motion: MotionEvent?): Boolean {
                if(motion!!.action ==MotionEvent.ACTION_DOWN){
                    cameraView.takeVideo(fileToUpload)
                    return true
                }
                else if(motion.action ==MotionEvent.ACTION_UP){
                    cameraView.stopVideo()
                    return true
                }
                return false
            }

        })

        cameraView.addCameraListener(object  : CameraListener(){
            override fun onVideoRecordingStart() {
                Toast.makeText(context, "Video Kaydediliyor", Toast.LENGTH_SHORT).show()
                super.onVideoRecordingStart()
            }

            override fun onVideoTaken(result: VideoResult) {
                Log.e("****************", "Video Kaydedildi onVideoTaken ")
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