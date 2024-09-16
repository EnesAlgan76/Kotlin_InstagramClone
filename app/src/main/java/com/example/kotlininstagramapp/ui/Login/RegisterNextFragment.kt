package com.example.kotlininstagramapp.ui.Login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentRegisterNextBinding
import com.example.kotlininstagramapp.ui.dialogs.NSCircleProgress
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


@AndroidEntryPoint
class RegisterNextFragment :Fragment(){
    private lateinit var args: RegisterNextFragmentArgs
    private var input: String = ""
    private var hint: String = ""

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterNextBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterNextBinding.inflate(inflater, container, false)

        args = RegisterNextFragmentArgs.fromBundle(requireArguments())
        hint = args.hint
        input = args.input


        binding.btnIleriFrgregister.setOnClickListener {
            val userName = binding.etFrgregisterKullaniciAdi.text.trim().toString()
            val password = binding.etFrgregisterSifre.text.trim().toString()
            val fullName = binding.etFrgregisterAdsoyad.text.trim().toString()

            if (checkFieldsAreFilled()) {
                val email = if (hint == "Email") input else ""
                val phoneNumber = if (hint == "Phone") input else ""

                viewModel.registerUser(userName, fullName, email, phoneNumber, password)
            } else {
                showToast("Tüm alanları doldurunuz")
            }

            observeRegisterState()
        }


        return binding.root
    }

    private fun observeRegisterState() {
        val nsdialog = NSCircleProgress(requireContext())
        viewModel.registerState.observe(requireActivity()) { state ->
            when (state) {
                is RegisterState.Loading -> nsdialog.showProgress()
                is RegisterState.Success -> {
                    nsdialog.hideProgress()
                    backToLogin()
                    showToast("Kayıt Başarılı!")
                    //startActivity(Intent(requireContext(), LoginActivity::class.java))
                }

                is RegisterState.Error -> {
                    nsdialog.hideProgress()
                    showToast("Hata. ${state.errorMessage}")
                }
            }
        }
    }



    private fun backToLogin() {
        findNavController().navigate(
            R.id.action_registerNextFragment_to_loginFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true) // Clears back stack up to LoginFragment
                .build()
        )
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
       // gelenTelNo = event.telNo?:""
       // gelenMail = event.mail?:""

    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }



}