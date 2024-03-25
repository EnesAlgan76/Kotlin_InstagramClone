package com.example.kotlininstagramapp.ui.Login

interface LoginCallback {
    fun onLoginSuccess()
    fun onLoginFailure(message: String)
}