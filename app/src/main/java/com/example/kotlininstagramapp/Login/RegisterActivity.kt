package com.example.kotlininstagramapp.Login

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.ActivityProfileBinding
import com.example.kotlininstagramapp.databinding.ActivityRegisterBinding
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleEmailTelClick()
        handleIleriButtonClick()
    }

    override fun onBackPressed() {
        binding.registerRoot.visibility= View.VISIBLE
        super.onBackPressed()
    }

    private fun handleIleriButtonClick() {
        binding.btnIleri.setOnClickListener {
            if (binding.etRegisterpage.hint=="Telefon"){
                binding.registerRoot.visibility= View.GONE
                binding.flRegisterpage.visibility = View.VISIBLE
                var transiction = supportFragmentManager.beginTransaction()
                transiction.replace(R.id.fl_registerpage, TelFragment())
                transiction.addToBackStack("telefonfragment")
                transiction.commit()
                EventBus.getDefault().postSticky(EventBusDataEvents.TelefonGonder(binding.etRegisterpage.text.toString()))
            }else{
                binding.registerRoot.visibility= View.GONE
                binding.flRegisterpage.visibility = View.VISIBLE
                var transiction = supportFragmentManager.beginTransaction()
                transiction.replace(R.id.fl_registerpage, MailFragment())
                transiction.addToBackStack("mailfragment")
                transiction.commit()
                EventBus.getDefault().postSticky(EventBusDataEvents.EmailGonder(binding.etRegisterpage.text.toString()))
            }

        }
    }

    private fun handleEmailTelClick() {
        binding.tvTel.setOnClickListener {
            binding.viewTel.setBackgroundColor(Color.BLACK)
            binding.viewMail.setBackgroundColor(Color.parseColor("#DFDFDF"))
            binding.etRegisterpage.apply {
                inputType = InputType.TYPE_CLASS_PHONE
                hint="Telefon"
            }
        }

        binding.tvEposta.setOnClickListener {
            binding.viewTel.setBackgroundColor(Color.parseColor("#DFDFDF"))
            binding.viewMail.setBackgroundColor(Color.BLACK)
            binding.etRegisterpage.apply {
                inputType = InputType.TYPE_CLASS_TEXT
                hint="E-posta"
            }
        }

    }
}