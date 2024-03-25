package com.example.kotlininstagramapp.data.api

data class BaseResponse(
    var status: Boolean = false,
    var message: String = "",
    var data: Any? = null
)
