package com.example.kotlininstagramapp.ui

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentCallBinding
import com.example.turkiyefinansappclone.video_call.repository.MainRepository
import com.example.turkiyefinansappclone.video_call.repository.MainService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class CallFragment : Fragment(), MainService.EndCallListener {

    private var target: String? = null
    private var callerName: String? = null
    private var isVideoCall: Boolean = true
    private var isCaller: Boolean = true

    private var isMicrophoneMuted = false
    private var isCameraMuted = false

    @Inject
    lateinit var serviceRepository: MainService

    @Inject
    lateinit var mainRepository: MainRepository

    private var _binding: FragmentCallBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAudioDeviceToSpeaker()

        arguments?.let {
            target = it.getString("target")
            isVideoCall = it.getBoolean("isVideoCall", true)
            isCaller = it.getBoolean("isCaller", true)
            callerName = it.getString("callerName", )
        } ?: run {
            activity?.onBackPressed()
        }

        serviceRepository = MainService(mainRepository)
        MainService.endCallListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun setAudioDeviceToSpeaker() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = true
    }

    private fun init() {
        binding.apply {
            callTitleTv.text =  getString(R.string.in_call_with, callerName)
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0..3600) {
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        callTimerTv.text = i.convertToHumanTime()
                    }
                }
            }

            if (!isVideoCall) {
                toggleCameraButton.isVisible = false
                switchCameraButton.isVisible = false
            }

            MainService.remoteSurfaceView = remoteView
            MainService.localSurfaceView = localView

            target?.let { serviceRepository.setupViews(isVideoCall, isCaller, it) }

            endCallButton.setOnClickListener {
                serviceRepository.sendEndCall()
            }

            switchCameraButton.setOnClickListener {
                serviceRepository.switchCamera()
            }
        }
        setupMicToggleClicked()
        setupCameraToggleClicked()
    }

    private fun setupMicToggleClicked() {
        binding.apply {
            toggleMicrophoneButton.setOnClickListener {
                if (!isMicrophoneMuted) {
                    serviceRepository.toggleAudio(true)
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_off)
                } else {
                    serviceRepository.toggleAudio(false)
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_on)
                }
                isMicrophoneMuted = !isMicrophoneMuted
            }
        }
    }

    private fun setupCameraToggleClicked() {
        binding.apply {
            toggleCameraButton.setOnClickListener {
                if (!isCameraMuted) {
                    serviceRepository.toggleVideo(true)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_on)
                } else {
                    serviceRepository.toggleVideo(false)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_off)
                }
                isCameraMuted = !isCameraMuted
            }
        }
    }

    private fun Int.convertToHumanTime(): String {
        val seconds = this % 60
        val minutes = this / 60
        val secondsString = if (seconds < 10) "0$seconds" else "$seconds"
        val minutesString = if (minutes < 10) "0$minutes" else "$minutes"

        return "$minutesString:$secondsString"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainService.remoteSurfaceView?.release()
        MainService.remoteSurfaceView = null

        MainService.localSurfaceView?.release()
        MainService.localSurfaceView = null

        _binding = null
    }

    override fun onCallEnded() {
        activity?.onBackPressed()
    }
}
