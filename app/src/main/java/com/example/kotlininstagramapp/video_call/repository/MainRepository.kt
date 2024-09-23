package com.example.turkiyefinansappclone.video_call.repository

import com.enesalgan.nswebrtc.NSWebRTCClient
import com.enesalgan.nswebrtc.interfaces.NSPeerObserver
import com.enesalgan.nswebrtc.models.NSDataModel
import com.enesalgan.nswebrtc.models.NSDataModelType.*
import com.example.turkiyefinansappclone.video_call.service.FirebaseClient
import com.example.turkiyefinansappclone.video_call.utils.UserStatus
import com.google.gson.Gson
import org.webrtc.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainRepository @Inject constructor(private val firebaseClient: FirebaseClient, private val webRTCClient: NSWebRTCClient, private val gson: Gson) : NSWebRTCClient.Listener {
    private var target: String? = null
    var mainRepositoryListener: MainRepositoryListener? = null
    private var remoteView:SurfaceViewRenderer?=null

    interface MainRepositoryListener {
        fun onLatestEventReceived(data: NSDataModel, callerName:String)
        fun endCall()
    }


    fun initWebrtcClient(userId: String) {
        webRTCClient.listener = this
        webRTCClient.initializeWebrtcClient(userId, object : NSPeerObserver() {

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                try {
                    p0?.videoTracks?.get(0)?.addSink(remoteView)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let {
                    webRTCClient.sendIceCandidate(target!!, it)
                }
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                super.onConnectionChange(newState)
                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                    changeMyStatus(UserStatus.IN_CALL)
                    firebaseClient.clearLatestEvent()
                }
            }
        })
    }


    fun initFirebase() {
        firebaseClient.subscribeForLatestEvent { event,callerName ->
                mainRepositoryListener?.onLatestEventReceived(event,callerName)
                when (event.type) {
                    Offer ->{
                        webRTCClient.onRemoteSessionReceived(
                            SessionDescription(
                                SessionDescription.Type.OFFER,
                                event.data.toString()
                            )
                        )
                        webRTCClient.answer(target!!)
                    }
                    Answer ->{
                        webRTCClient.onRemoteSessionReceived(
                            SessionDescription(
                                SessionDescription.Type.ANSWER,
                                event.data.toString()
                            )
                        )
                    }
                    IceCandidates->{
                        val candidate: IceCandidate? = try {
                            gson.fromJson(event.data.toString(),IceCandidate::class.java)
                        }catch (e:Exception){
                            null
                        }
                        candidate?.let {
                            webRTCClient.addIceCandidateToPeer(it)
                        }
                    }
                    EndCall->{
                        mainRepositoryListener?.endCall()
                    }
                    else -> Unit
                }

        }
    }

    fun stopFirebase(){
        firebaseClient.unsubscribeFromLatestEvent()
    }

    fun stopWebrtcClient() {
        webRTCClient.closeConnection()
    }


    fun sendConnectionRequest(target: String, isVideoCall: Boolean, success: (Boolean) -> Unit) {
        firebaseClient.sendMessageToOtherClient(
            NSDataModel(
                type = if (isVideoCall) StartVideoCall else StartAudioCall,
                target = target
            ), success
        )
    }

    fun setTarget(target: String) {
        this.target = target
    }

    fun initLocalSurfaceView(view: SurfaceViewRenderer, isVideoCall: Boolean) {
        webRTCClient.initLocalSurfaceView(view, isVideoCall)
    }

    fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        webRTCClient.initRemoteSurfaceView(view)
        this.remoteView = view
    }

    fun startCall() {
        webRTCClient.call(target!!)
    }

    fun endCall() {
        webRTCClient.closeConnection()
        changeMyStatus(UserStatus.ONLINE)
    }

    fun sendEndCall() {
        onTransferEventToSocket(
            NSDataModel(
                type = EndCall,
                target = target!!
            )
        )
    }

    private fun changeMyStatus(status: UserStatus) {
        firebaseClient.changeMyStatus(status)
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        webRTCClient.toggleAudio(shouldBeMuted)
    }

    fun toggleVideo(shouldBeMuted: Boolean) {
        webRTCClient.toggleVideo(shouldBeMuted)
    }

    fun switchCamera() {
        webRTCClient.switchCamera()
    }


    override fun onTransferEventToSocket(data: NSDataModel) {
        firebaseClient.sendMessageToOtherClient(data) {}
    }


}