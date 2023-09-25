package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.utils.PrefUtil

class ThemeViewModel : ViewModel() {
    private val _darkThemeEnabled = MutableLiveData(PrefUtil.getAppTheme())
    val darkThemeEnabled: LiveData<Boolean> = _darkThemeEnabled

    fun toggleDarkTheme() {
        _darkThemeEnabled.value = !_darkThemeEnabled.value!!
        PrefUtil.setAppTheme(darkThemeEnabled.value!!)
    }
}