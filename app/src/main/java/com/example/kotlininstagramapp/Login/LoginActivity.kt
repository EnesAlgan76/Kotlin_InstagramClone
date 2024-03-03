package com.example.kotlininstagramapp.Login

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.Home.HomeActivity
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.api.RetrofitInstance
import com.example.kotlininstagramapp.api.RetrofitInstance.retrofit
import com.example.kotlininstagramapp.api.UserApi
import com.example.kotlininstagramapp.api.UserModel
import com.example.kotlininstagramapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var buttonActive :Boolean = false
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userService = RetrofitInstance.retrofit.create(UserApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegiserlogin.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        binding.etLoginmail.addTextChangedListener(textWatcher)
        binding.etLoginpassword.addTextChangedListener(textWatcher)

        binding.btnLogingiris.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (buttonActive){
                    val response= userService.authenticateUser(binding.etLoginmail.text.toString(),binding.etLoginpassword.text.toString()).execute()
                    if (response.isSuccessful) {
                        println("Response Body: ${response.body()}")
                        if (response.body()?.data != null) {
                            println("Logging ...")
                            val userDataJson = Gson().toJson(response.body()?.data)
                            val userModel = Gson().fromJson(userDataJson, UserModel::class.java)
                            UserSingleton.userModel = userModel
                            loginUserWithEmail(userModel.password,userModel.email) // Firebase Auth
                        }else{
                            println("User not found")
                        }
                    } else {
                        println("Error Response: ${response.errorBody()?.string()}")
                    }
                }
            }


            /*if(buttonActive){
                var fieldList = arrayListOf("telNo", "mail", "userName")
                searchUserRecursively(binding.etLoginmail.text.toString(), binding.etLoginpassword.text.toString(), fieldList)
            }*/

        }

    }


    /*private fun searchUserRecursively(etLoginmail: String, etLoginpassword: String, fieldList: ArrayList<String>) {
        if (fieldList.isEmpty()){
            Toast.makeText(this, "Kullanıcı Bulunamadı", Toast.LENGTH_SHORT).show()
            return
        }
        var field = fieldList.first()
        val userQuery =firestore.collection("users").whereEqualTo(field,etLoginmail)

        userQuery.get().addOnSuccessListener {documents ->
            if(!documents.isEmpty){
                val doc= documents.documents.get(0)
                val mail = doc.getString("mail")
                loginUserWithEmail(etLoginpassword,mail)
            }else{
                fieldList.removeFirst()
                searchUserRecursively(etLoginmail,etLoginpassword,fieldList)
            }

        }
    }*/

    private fun loginUserWithEmail(password: String?, mail: String?) {
        if(password!=null && mail!=null){
            auth.signInWithEmailAndPassword(mail,password)
                .addOnSuccessListener {
                    showToast("GİRİŞ BAŞARILI")
                    startActivity(Intent(this,HomeActivity()::class.java))
                    retrieveCurrentFcmToken()
                    finish()
            }
                .addOnFailureListener { exception->
                    showToast("Şifre Yanlış")
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
        }

    }




    private fun retrieveCurrentFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("retrieveCurrentFcmToken ", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("retrieveCurrentFcmToken", "------>> "+token)
            //FirebaseHelper().saveNewToken(token)
            CoroutineScope(Dispatchers.IO).launch {


                try {
                    val response = userService.updateFcmToken(UserSingleton.userModel!!.userId, token).execute()

                    Log.e("TOKEN GÜNCELLENDİ", "body : "+response.body())

                } catch (e: Exception) {
                    Log.e("TOKEN GÜNCELLENEMEDİ", "Error updating FCM token: ${e.message}")
                    throw e
                }
            }

        })
    }


    private val textWatcher= object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if(binding.etLoginmail.length()<6 || binding.etLoginpassword.length() <6){
                binding.btnLogingiris.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.btnLogingiris.setTextColor(Color.parseColor("#3a97f1"))
                buttonActive = false

            }else{
                binding.btnLogingiris.setBackgroundColor(Color.parseColor("#3a97f1"))
                binding.btnLogingiris.setTextColor(Color.WHITE)
                buttonActive = true
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}