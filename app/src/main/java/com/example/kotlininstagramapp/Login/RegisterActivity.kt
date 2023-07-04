package com.example.kotlininstagramapp.Login

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityProfileBinding
import com.example.kotlininstagramapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleEmailTelClick()
    }

    private fun handleEmailTelClick() {
        binding.tvTel.setOnClickListener {
            binding.viewTel.setBackgroundColor(Color.BLACK)
            binding.viewMail.setBackgroundColor(Color.parseColor("#DFDFDF"))
            binding.etRegisterpage.apply {
                inputType = InputType.TYPE_CLASS_PHONE
                hint="Tel"
            }
        }

        binding.tvEposta.setOnClickListener {
            binding.viewTel.setBackgroundColor(Color.parseColor("#DFDFDF"))
            binding.viewMail.setBackgroundColor(Color.BLACK)
            binding.etRegisterpage.apply {
                inputType = InputType.TYPE_NULL
                hint="E-posta"
            }
        }

    }
}