package com.example.kotlininstagramapp.ui.Login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.data.api.RetrofitInstance
import com.example.kotlininstagramapp.data.api.UserApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentLoginBinding
import com.example.kotlininstagramapp.ui.dialogs.NSCircleProgress


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var buttonActive: Boolean = false
    private val firestore = FirebaseFirestore.getInstance()
    private val userService = RetrofitInstance.retrofit.create(UserApi::class.java)
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegiserlogin.setOnClickListener {
            // Navigate to RegisterActivity or RegisterFragment
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.etLoginmail.addTextChangedListener(textWatcher)
        binding.etLoginpassword.addTextChangedListener(textWatcher)

        binding.btnLogingiris.setOnClickListener {
            if (buttonActive) {
                val email = binding.etLoginmail.text.toString()
                val password = binding.etLoginpassword.text.toString()
                viewModel.loginUser(email, password)
            }
        }

        observeLoginState()
    }




    private fun observeLoginState() {
        val nsdialog = NSCircleProgress(requireContext())
        viewModel.loginState.observe(requireActivity()) { state ->
            when (state) {
                is LoginState.Loading -> nsdialog.showProgress()
                is LoginState.Success -> {
                    nsdialog.hideProgress()

                   //val intent = Intent(requireContext(), HomeActivity::class.java)
                   //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                   //startActivity(intent)

                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    retrieveCurrentFcmToken()
                  //  finish()
                }

                is LoginState.Error -> {
                    nsdialog.hideProgress()
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

            val secondaryColor = ContextCompat.getColor(requireContext(), R.color.nscolorSecondary)
            val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)

            if(binding.etLoginmail.length()<6 || binding.etLoginpassword.length() <6){
                binding.btnLogingiris.setBackgroundColor(whiteColor)
                binding.btnLogingiris.setTextColor(secondaryColor)
                buttonActive = false

            }else{
                binding.btnLogingiris.setBackgroundColor(secondaryColor)
                binding.btnLogingiris.setTextColor(whiteColor)
                buttonActive = true
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    private fun showToast(message: String) {
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


