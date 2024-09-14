package com.example.kotlininstagramapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.data.model.UserModel
import com.example.kotlininstagramapp.databinding.FragmentCheckAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CheckAuthFragment : Fragment() {

    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var userService: UserApi
    lateinit var binding: FragmentCheckAuthBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCheckAuthBinding.inflate(layoutInflater)

        CoroutineScope(Dispatchers.IO).launch {
            setupCurrentUser()
        }
        return binding.root


    }


    private suspend fun setupCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserFromDatabase(currentUser.uid)
        } else {
            goToLoginPage()
        }
    }

    private suspend fun fetchUserFromDatabase(userId: String) {

            var userData: Any? = null
            try {
                val response = userService.getUserById(userId).execute()
                if (response.isSuccessful) {
                    Log.e("Spring Response", response.body().toString())
                    userData = response.body()?.data
                    if(userData==null){
                        goToLoginPage()
                    }
                } else {
                    handleConnectionError("Response unsuccesful")
                }
            } catch (e: Exception) {
                handleConnectionError(e.toString())
            }
            userData?.let { handleUserResponse(it) } //?: handleUserNotFound()

    }


    private suspend fun handleUserResponse(userData: Any) {
        val userModel = toUserModel(userData)
        UserSingleton.userModel = userModel
        withContext(Dispatchers.Main){
           //val intent = Intent(requireContext(), HomeActivity::class.java)
           //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
           //startActivity(intent)

            findNavController().navigate(R.id.action_checkAuthFragment_to_homeFragment)
        }

    }

    suspend fun goToLoginPage(){
        withContext(Dispatchers.Main){
            findNavController().navigate(R.id.action_chechAuthFragment_to_loginFragment)
        }

    }

    private suspend fun handleConnectionError(e: String) {
        withContext(Dispatchers.Main){
            Toast.makeText(requireContext(), "Bağlantı Hatası", Toast.LENGTH_SHORT).show()
        }
        Log.e("Hata",e)
        goToLoginPage()
    }

    private fun toUserModel(userData: Any): UserModel {
        val userDataJson = Gson().toJson(userData)
        return Gson().fromJson(userDataJson, UserModel::class.java)
    }

}