package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    private val _darkThemeEnabled = MutableLiveData(false)
    val darkThemeEnabled: LiveData<Boolean> = _darkThemeEnabled

    fun toggleDarkTheme() {
        _darkThemeEnabled.value = !_darkThemeEnabled.value!!
    }
}