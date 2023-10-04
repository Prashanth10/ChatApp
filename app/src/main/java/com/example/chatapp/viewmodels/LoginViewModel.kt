package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.dataclass.UserData
import com.example.chatapp.firebase.AccountOperations

class LoginViewModel(private val accountOperations: AccountOperations): ViewModel() {
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    fun loginUser(userData: UserData) {
//        accountOperations.loginUser(userData, successCallback = {success ->
//            _loginSuccess.value = success})
        accountOperations.loginUser(userData) { success ->
            _loginSuccess.value = success
        }
    }
}