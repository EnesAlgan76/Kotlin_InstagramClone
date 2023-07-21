package com.example.kotlininstagramapp.utils

import com.example.kotlininstagramapp.Models.UserDetails

class EventBusDataEvents {
    class KayitBilgileriGonder(var telNo: String?, var mail: String?)
    class KullaniciBilgileriGonder(var userDetails: UserDetails, var full_name:String, var user_name:String)
   // class EmailGonder(val mail: String)
}
