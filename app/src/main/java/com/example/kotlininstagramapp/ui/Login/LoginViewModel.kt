package com.example.kotlininstagramapp.ui.Login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlininstagramapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel()  {

    fun loginUser(email: String, password: String, callback: LoginCallback) {
        viewModelScope.launch {
            val userExists = userRepository.checkUserExists(email, password)
            if (userExists!=null) {
                Log.e("Spring", "kullanıcı bulundu, firebase giriş yapılıyor ...")
                userRepository.loginUserWithEmail(email, password, object :LoginCallback{
                    override fun onLoginSuccess() {
                        callback.onLoginSuccess()
                    }
                    override fun onLoginFailure(message: String) {
                        callback.onLoginFailure(message + email)
                    }
                })
            } else {
                callback.onLoginFailure("User not found.")
            }
        }
    }


}

