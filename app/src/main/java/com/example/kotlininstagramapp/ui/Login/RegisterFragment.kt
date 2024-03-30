package com.example.kotlininstagramapp.ui.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kotlininstagramapp.Home.HomeActivity
import com.example.kotlininstagramapp.databinding.FragmentRegisterBinding
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


@AndroidEntryPoint
class RegisterFragment :Fragment(){

    private var gelenTelNo: String = ""
    private var gelenMail: String = ""

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)


        binding.btnIleriFrgregister.setOnClickListener {
            val userName = binding.etFrgregisterAdsoyad.text.trim().toString()
            val password = binding.etFrgregisterSifre.text.trim().toString()
            val fullName = binding.etFrgregisterAdsoyad.text.trim().toString()

            if (!checkFieldsAreFilled()) {
                showToast("Tüm Alanları Doldurunuz")
            }else {
                println(userName+"---"+password+"---"+fullName)
                viewModel.registerUser(userName,fullName, gelenMail,gelenTelNo,password)
            }

            observeLoginState()
        }


        return binding.root
    }

    private fun observeLoginState() {
        viewModel.registerState.observe(requireActivity()) { state ->
            when (state) {
                is RegisterState.Loading -> println("Register İşlem Devam Ediyor ...")
                is RegisterState.Success -> {
                    showToast("Kayıt Başarılı")
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                }

                is RegisterState.Error -> {
                    showToast("Hata. ${state.errorMessage}")
                }
            }
        }
    }









    private fun checkFieldsAreFilled(): Boolean {
        val adSoyadText = binding.etFrgregisterAdsoyad.text?.trim().toString()
        val kullaniciAdiText = binding.etFrgregisterKullaniciAdi.text?.trim().toString()
        val sifreText = binding.etFrgregisterSifre.text?.trim().toString()

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