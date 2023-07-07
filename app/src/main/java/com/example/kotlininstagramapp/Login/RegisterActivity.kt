package com.example.kotlininstagramapp.Login

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
       supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

//         bu işlemler sırasıyla bir önceki fragmenti göstercek şekilde ayarlandı.
//        if ((this.supportFragmentManager.backStackEntryCount) > 1) {
//            this.supportFragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//            binding.registerRoot.visibility= View.VISIBLE
//        }

    }

    //Bu da farklı bir kullanım. varysayalım 3 tane fragment açılmış olsun ve 3 fragment açık iken geri gitmek istediğinde
    // 2. fragment atlayarak direkt 1. fragmenti göstermek istersen bu yapıyı kullanırsın
//    override fun onBackPressed() {
//        if ((supportFragmentManager.backStackEntryCount) > 1) {
//            // Get the name of the topmost fragment in the back stack
//            val topFragmentName = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
//
//            if (topFragmentName == "RegisterFragment") {
//                // Pop the fifth fragment from the back stack
//                supportFragmentManager.popBackStack("Göstermek istediğin fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//            } else {
//                supportFragmentManager.popBackStack()
//            }
//        } else {
//            super.onBackPressed()
//            binding.registerRoot.visibility = View.VISIBLE
//        }
//    }

    private fun handleIleriButtonClick() {
        binding.btnIleri.setOnClickListener {
            if (binding.etRegisterpage.hint=="Telefon"){
                binding.registerRoot.visibility= View.GONE
                binding.flRegisterpage.visibility = View.VISIBLE

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_registerpage, TelFragment())
                    .addToBackStack("TelFragment")
                    .commit()

                EventBus.getDefault().postSticky(EventBusDataEvents.KayitBilgileriGonder(binding.etRegisterpage.text.toString(),null))
            }else{
                binding.registerRoot.visibility= View.GONE
                binding.flRegisterpage.visibility = View.VISIBLE

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_registerpage, MailFragment())
                    .addToBackStack("MailFragment")
                    .commit()

                EventBus.getDefault().postSticky(EventBusDataEvents.KayitBilgileriGonder(null,binding.etRegisterpage.text.toString()))
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