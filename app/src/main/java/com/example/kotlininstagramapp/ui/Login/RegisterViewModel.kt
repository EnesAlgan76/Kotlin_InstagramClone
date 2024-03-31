package com.example.kotlininstagramapp.ui.Login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlininstagramapp.data.api.RetrofitInstance
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.data.model.UserModel
import com.example.kotlininstagramapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState
    fun registerUser(userName: String,fullName:String, email: String, phoneNumber: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
             try {
                 val userExists = userRepository.checkUserExists(userName,email, phoneNumber)
                 if(userExists){
                     _registerState.value = RegisterState.Error("Bu kullanıcı adı ya da e-posta ile zaten bir hesap bulunmaktadır.")
                 }else{
                     userRepository.registerUser(userName,fullName, email, phoneNumber, password)
                     _registerState.value = RegisterState.Success
                 }
             }catch (e :Exception){
                 _registerState.value = RegisterState.Error(e.message ?: "An error occurred")
             }


        }
    }


}