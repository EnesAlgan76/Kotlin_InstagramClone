package com.example.kotlininstagramapp.Models

class User(
    var userId: String,
    var userFullName: String,
    var userName: String,
    var telNo: String,
    var mail: String,
    var password :String,
    var userDetails: UserDetails
){
    companion object {
        fun fromMap(map: Map<String, Any>): User {
            val userId = map["userId"] as String
            val userFullName = map["userFullName"] as String
            val userName = map["userName"] as String
            val telNo = map["telNo"] as String
            val mail = map["mail"] as String
            val password = map["password"] as String
            val userDetailsMap = map["userDetails"] as Map<String, Any>
            val userDetails = UserDetails.fromMap(userDetailsMap)

            return User(userId, userFullName, userName, telNo, mail, password, userDetails)
        }


    }

    override fun toString(): String {
        return "User(userId='$userId', userFullName='$userFullName', userName='$userName', " +
                "telNo='$telNo', mail='$mail', password='$password', userDetails=$userDetails)"
    }
}