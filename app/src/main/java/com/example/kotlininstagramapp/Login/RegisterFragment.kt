package com.example.kotlininstagramapp.Login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Models.UserDetails
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class RegisterFragment :Fragment(){
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private var adSoyad: EditText? = null
    private var kullaniciAdi: EditText? = null
    private var sifre: EditText? = null
    private var btnIleri: Button? = null

    private var gelenTelNo: String = ""
    private var gelenMail: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        adSoyad = view.findViewById(R.id.et_frgregister_adsoyad)
        kullaniciAdi = view.findViewById(R.id.et_frgregister_kullaniciAdi)
        sifre = view.findViewById(R.id.et_frgregister_sifre)
        btnIleri = view.findViewById(R.id.btn_ileri_frgregister)
        btnIleri?.setOnClickListener { registerNewUser() }
        return view
    }



    private fun registerNewUser() {
        if(checkFieldsAreFilled()){
            if (gelenMail.isEmpty()){gelenMail = gelenTelNo+"@enes.com"}
            val emailQuery = db.collection("users").whereEqualTo("userName",kullaniciAdi!!.text.trim().toString())
            emailQuery.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val querySnapshot = task.result
                    if(querySnapshot !=null && !querySnapshot.isEmpty){
                        showToast("Username already exists")
                    }else{
                        createFirebaseUser()
                    }
                }
            }
        }else{
            showToast("Tüm Alanları Doldurunuz")
        }
    }



    private fun createFirebaseUser() {
        firebaseAuth.createUserWithEmailAndPassword(gelenMail,sifre?.text.toString())
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                val userID = firebaseAuth.currentUser!!.uid

                val userDetails = UserDetails(0,0,0,"","")
                val user = User(userID, adSoyad?.text.toString(), kullaniciAdi?.text.toString(), gelenTelNo, gelenMail, sifre?.text.toString(),userDetails)

                val userCollection = db.collection("users")
                userCollection.document(userID).set(user).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        showToast("Yeni Kulanıcı Oluşturuldu")
                        requireActivity().finish()
                    }else{
                        showToast("Kullanıcı Veritabanınagmai Kaydedilirken Hata Oluştu")
                        firebaseAuth.currentUser!!.delete()
                    }
                }
            }else{
                showToast("Kullanıcı Kaydedilirken Hata Oluştu")
            }

        }
    }

    private fun checkFieldsAreFilled(): Boolean {
        val adSoyadText = adSoyad?.text?.trim().toString()
        val kullaniciAdiText = kullaniciAdi?.text?.trim().toString()
        val sifreText = sifre?.text?.trim().toString()

        return !(adSoyadText.isEmpty() || kullaniciAdiText.isEmpty() || sifreText.isEmpty())
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
    fun onTelefonGonderReceived2(event : EventBusDataEvents.KayitBilgileriGonder){
        gelenTelNo = event.telNo?:""
        gelenMail = event.mail?:""

    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }



}