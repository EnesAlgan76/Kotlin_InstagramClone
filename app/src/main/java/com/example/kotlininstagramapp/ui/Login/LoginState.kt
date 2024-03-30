package com.example.kotlininstagramapp.ui.Login

sealed class LoginState {
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val errorMessage: String) : LoginState()
}
