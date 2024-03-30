package com.example.kotlininstagramapp.ui.Login

sealed class RegisterState {
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val errorMessage: String) : RegisterState()
}