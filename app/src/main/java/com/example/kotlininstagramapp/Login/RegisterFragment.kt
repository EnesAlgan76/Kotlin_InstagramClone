package com.example.kotlininstagramapp.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.Home.HomeActivity
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

class RegisterFragment :Fragment(){
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_register,container,false)
        var ad_soyad = view.findViewById<EditText>(R.id.et_frgregister_adsoyad)
        var kullanici_adi = view.findViewById<EditText>(R.id.et_frgregister_kullaniciAdi)
        var sifre = view.findViewById<EditText>(R.id.et_frgregister_sifre)
        var btn_ileri =  view.findViewById<Button>(R.id.btn_ileri_frgregister)




       // requestVerificationCode(gelenTelNo)
       // btn_ileri.setOnClickListener { performOperations(et_onaykod.text.toString())}

        return view
    }

//    private fun performOperations(gelenKodKullanici: String) {
//        var credential = PhoneAuthProvider.getCredential(gelenVerificationId,gelenKodKullanici)
//        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
//            if (task.isSuccessful) {
//                Toast.makeText(activity, "Doğrulama Başarılı--> "+gelenKod, Toast.LENGTH_SHORT).show()
//                val user = task.result?.user
//            } else {
//                Toast.makeText(activity, "Hatalı Kod", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


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
            var gelenTelNo = event.telNo!!
        }


    }



}