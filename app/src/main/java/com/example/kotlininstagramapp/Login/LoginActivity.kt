package com.example.kotlininstagramapp.Login

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.kotlininstagramapp.Home.HomeActivity
import com.example.kotlininstagramapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var buttonActive :Boolean = false
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etLoginmail.addTextChangedListener(textWatcher)
        binding.etLoginpassword.addTextChangedListener(textWatcher)

        binding.btnLogingiris.setOnClickListener {
            if(buttonActive){
                var fieldList = arrayListOf("telNo", "mail", "userName")
                searchUserRecursively(binding.etLoginmail.text.toString(), binding.etLoginpassword.text.toString(), fieldList)
            }

        }

    }


    private fun searchUserRecursively(etLoginmail: String, etLoginpassword: String, fieldList: ArrayList<String>) {
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
    }

    private fun loginUserWithEmail(password: String?, mail: String?) {
        if(password!=null && mail!=null){
            auth.signInWithEmailAndPassword(mail,password)
                .addOnSuccessListener {
                    showToast("GİRİŞ BAŞARILI")
                    startActivity(Intent(this,HomeActivity()::class.java))
                    finish()
            }
                .addOnFailureListener { exception->
                    showToast("Şifre Yanlış")
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
        }

    }


    private val textWatcher= object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if(binding.etLoginmail.length()<6 || binding.etLoginpassword.length() <6){
                binding.btnLogingiris.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.btnLogingiris.setTextColor(Color.parseColor("#3a97f1"))

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