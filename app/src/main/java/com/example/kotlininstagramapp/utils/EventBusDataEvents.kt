package com.example.kotlininstagramapp.utils

import com.example.kotlininstagramapp.Models.Story
import com.example.kotlininstagramapp.Models.UserDetails
import java.io.File

class EventBusDataEvents {
    class KayitBilgileriGonder(var telNo: String?, var mail: String?)
    class KullaniciBilgileriGonder(var full_name:String, var user_name:String, var biografi:String, var profilePicture:String)
   // class EmailGonder(val mail: String)

    class SendMediaFile(var mediaFile : File)


    class SendStories(var stories : List<Story>)
}
