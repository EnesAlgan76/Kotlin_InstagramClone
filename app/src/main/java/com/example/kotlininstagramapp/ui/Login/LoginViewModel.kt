package com.example.kotlininstagramapp.ui.Login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val userModel = userRepository.authenticateUser(email,password)
                if (userModel != null) {
                    val firebaseUser = userRepository.loginUserWithFirebase(userModel.email, password)
                    if(firebaseUser!=null){
                        _loginState.value = LoginState.Success
                        UserSingleton.userModel = userModel
                    }
                } else {
                    _loginState.value = LoginState.Error("User not found in the database")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Authentication failed")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

