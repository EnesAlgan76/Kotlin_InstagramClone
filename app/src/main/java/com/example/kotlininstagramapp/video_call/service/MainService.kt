package com.example.turkiyefinansappclone.video_call.repository

import com.enesalgan.nswebrtc.models.NSDataModel
import com.enesalgan.nswebrtc.models.NSDataModelType
import com.example.kotlininstagramapp.Generic.UserSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainService @Inject constructor(private val mainRepository: MainRepository):MainRepository.MainRepositoryListener {

    companion object {
        var incomingCallListener: IncomingCallListener? = null
        var endCallListener: EndCallListener?=null
        var localSurfaceView: SurfaceViewRenderer?=null
        var remoteSurfaceView: SurfaceViewRenderer?=null
    }

    interface EndCallListener {
        fun onCallEnded()
    }


    interface IncomingCallListener {
        fun onCallReceived(model: NSDataModel, callerName: String)
    }

    fun startService(userId: String) {
        mainRepository.mainRepositoryListener = this
        CoroutineScope(Dispatchers.IO).launch {
            mainRepository.initWebrtcClient(userId)
            mainRepository.initFirebase()
        }
    }

    fun stopService() {
        mainRepository.stopWebrtcClient()
        mainRepository.stopFirebase()
    }


    fun setupViews(videoCall: Boolean, caller: Boolean, target: String) {
        mainRepository.setTarget(target)
        mainRepository.initLocalSurfaceView(localSurfaceView!!,videoCall)
        mainRepository.initRemoteSurfaceView(remoteSurfaceView!!)
        if (!caller){
            mainRepository.startCall()
        }
    }




    fun sendEndCall() {
        mainRepository.sendEndCall()
        mainRepository.endCall()
        endCallListener?.onCallEnded()
        mainRepository.initWebrtcClient(UserSingleton.userModel!!.userId)
    }



    fun switchCamera() {
        mainRepository.switchCamera()
    }

    fun toggleAudio(shouldBeMuted: Boolean) {

        mainRepository.toggleAudio(shouldBeMuted)

    }

    fun toggleVideo(shouldBeMuted: Boolean) {
        mainRepository.toggleVideo(shouldBeMuted)
    }

    override fun onLatestEventReceived(data: NSDataModel, callerName:String) {
        if (data.isValid()) {
            when (data.type) {
                NSDataModelType.StartVideoCall,
                NSDataModelType.StartAudioCall -> {
                    incomingCallListener?.onCallReceived(data, callerName)
                }
                else -> Unit
            }
        }
    }

    override fun endCall() {
        mainRepository.endCall()
        endCallListener?.onCallEnded()
        mainRepository.initWebrtcClient(UserSingleton.userModel!!.userId)
    }



}