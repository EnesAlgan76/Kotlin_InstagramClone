package com.example.kotlininstagramapp.Generic

import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.api.UserModel

object UserSingleton {
    var user: User? = null
    var userModel: UserModel? = null
}