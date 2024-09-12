package com.example.kotlininstagramapp.ui.Login

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.kotlininstagramapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class TelFragment : DialogFragment() {
    var gelenTelNo = ""
    var gelenVerificationId = ""
    var gelenKod = ""

    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the layout for this fragment
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_tel, null)
        val tv_telNo = view.findViewById<TextView>(R.id.tv_fragment_tel)
        val btn_ileri = view.findViewById<Button>(R.id.btn_ileri_frgregister)
        val et_onaykod = view.findViewById<EditText>(R.id.et_frg_onaykodu)
        tv_telNo.text = gelenTelNo

        requestVerificationCode("+90" + gelenTelNo)
        btn_ileri.setOnClickListener { performOperations(et_onaykod.text.toString()) }

        return android.app.AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Telefon Doğrulama")
            .setPositiveButton("OK", null) // Optional: Add a positive button if needed
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() } // Add a negative button to dismiss the dialog
            .create()
    }

    private fun performOperations(gelenKodKullanici: String) {
        try {
            val credential = PhoneAuthProvider.getCredential(gelenVerificationId, gelenKodKullanici)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Doğrulama Başarılı--> " + gelenKod, Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                    user?.delete()
                    // Navigate to the next fragment or activity if needed
                } else {
                    Toast.makeText(activity, "Hatalı Kod", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("ENES ERROR", "")
        }
    }

    private fun requestVerificationCode(gelenTelNo: String) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                gelenKod = p0.smsCode!!
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.e(p0.message, "")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                gelenVerificationId = verificationId
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(gelenTelNo)
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
            .setActivity(requireActivity()) // Your activity
            .setCallbacks(callbacks) // Verification callbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}
