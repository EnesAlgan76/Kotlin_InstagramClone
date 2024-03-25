package com.example.kotlininstagramapp.ui.Login

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
import com.example.kotlininstagramapp.api.RetrofitInstance
import com.example.kotlininstagramapp.Services.UserApi
import com.example.kotlininstagramapp.api.model.UserModel
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        btnIleri?.setOnClickListener { registerNewUserSpring() }
        return view
    }



    private fun registerNewUserSpring() {
        if (!checkFieldsAreFilled()) {
            showToast("Tüm Alanları Doldurunuz")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userService = RetrofitInstance.retrofit.create(UserApi::class.java)
                val userName = kullaniciAdi!!.text.trim().toString()

                val call = userService.checkUserExists(userName, gelenMail, gelenTelNo)
                println("Request Url : ${call.request().url()}")

                val response = call.execute()
                if (response.isSuccessful) {
                    if (response.body() == true) {
                        println("Bu kullanıcı adı ya da e-posta ile zaten bir hesap bulunmaktadır.")
                    } else {
                        println("Yeni hesap oluşturuluyor...")
                        createUserSpring(userService, userName)
                    }
                } else {
                    println("Hata Mesajı: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Hata Oluştu: ${e.message}")
               // showToast("Bağlantı Hatası")
            }
        }
    }

    private fun createUserSpring(userService: UserApi, userName: String) {
        if (gelenMail.isEmpty()){gelenMail = gelenTelNo+"@enes.com"}

        firebaseAuth.createUserWithEmailAndPassword(gelenMail,sifre?.text.toString())
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    val userID = firebaseAuth.currentUser!!.uid
                    val userModel = UserModel(
                        userId =userID ,
                        userName = userName,
                        password = sifre?.text.toString(),
                        phoneNumber = gelenTelNo,
                        email = gelenMail,
                        fullName = adSoyad?.text.toString(),
                        fcmToken = "",
                        profilePicture = "",
                        biography = "",
                        followerCount = 0,
                        postCount = 0,
                        followingCount = 0
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val createCall = userService.createUser(userModel)
                        val createResponse = createCall.execute()
                        if (createResponse.isSuccessful) {
                            println("Hesap başarıyla oluşturuldu.")
                            println("User create body : ${createResponse.body()}")
                        } else {
                            println("Hata Mesajı : ${createResponse.errorBody()?.string()}")
                        }
                    }



                }
            }

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