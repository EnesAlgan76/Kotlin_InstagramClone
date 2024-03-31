package com.example.kotlininstagramapp.ui.Login

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
import com.example.kotlininstagramapp.data.api.RetrofitInstance
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.data.model.UserModel
import com.example.kotlininstagramapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.activity.viewModels


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var buttonActive :Boolean = false
    val firestore = FirebaseFirestore.getInstance()
    val userService = RetrofitInstance.retrofit.create(UserApi::class.java)
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegiserlogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.etLoginmail.addTextChangedListener(textWatcher)
        binding.etLoginpassword.addTextChangedListener(textWatcher)

        binding.btnLogingiris.setOnClickListener {
            if (buttonActive){
                val email = binding.etLoginmail.text.toString()
                val password = binding.etLoginpassword.text.toString()
                viewModel.loginUser(email, password)
            }

        }

        observeLoginState()
    }



    private fun observeLoginState() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> println("Login İşlemi Devam Ediyor ...")
                is LoginState.Success -> {
                    showToast("Giriş Başarılı")
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    retrieveCurrentFcmToken()
                    finish()
                }

                is LoginState.Error -> {
                    showToast("Hata. ${state.errorMessage}")
                }
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