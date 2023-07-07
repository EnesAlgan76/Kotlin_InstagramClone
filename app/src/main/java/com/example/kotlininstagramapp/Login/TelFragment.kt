package com.example.kotlininstagramapp.Login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

class TelFragment :Fragment(){
    var gelenTelNo = ""
    var gelenVerificationId = ""
    var gelenKod = ""

    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_tel,container,false)
        var tv_telNo = view.findViewById<TextView>(R.id.tv_fragment_tel)
        var btn_ileri = view.findViewById<Button>(R.id.btn_ileri_frgregister)
        var et_onaykod = view.findViewById<EditText>(R.id.et_frg_onaykodu)
        tv_telNo.text = gelenTelNo

        requestVerificationCode(gelenTelNo)
        btn_ileri.setOnClickListener { performOperations(et_onaykod.text.toString())}

        return view
    }

    private fun performOperations(gelenKodKullanici: String) {
       // navigateRegisterFragment()
        try {
            var credential = PhoneAuthProvider.getCredential(gelenVerificationId,gelenKodKullanici)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Doğrulama Başarılı--> "+gelenKod, Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                    navigateRegisterFragment()
                } else {
                    Toast.makeText(activity, "Hatalı Kod", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e : java.lang.Exception){
            Log.e("ENES ERROR","")
        }
    }

    private fun navigateRegisterFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fl_registerpage,RegisterFragment())
            .addToBackStack("RegisterFragmentTel")
            .commit()

    }


    override fun onAttach(context: Context) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }


    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }


    @Subscribe(sticky = true)
     fun onTelefonGonderReceived(event : EventBusDataEvents.KayitBilgileriGonder){
        if (event.telNo!=null){
            gelenTelNo = event.telNo!!
        }


    }





    private fun requestVerificationCode(gelenTelNo: String) {

        val callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                gelenKod = p0.smsCode!!
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.e(p0.message,"")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                gelenVerificationId = verificationId
            }

        }


        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(gelenTelNo)
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
            .setActivity(this.requireActivity()) // Your activity
            .setCallbacks(callbacks) // Verification callbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }



}