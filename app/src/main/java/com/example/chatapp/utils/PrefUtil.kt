package com.example.chatapp.utils

import com.example.chatapp.constants.Constants
import com.example.chatapp.prefs.PrefManager

object PrefUtil {
    fun setLoggedIn(status: Boolean) =
        PrefManager.put(Constants.KEY_IS_SIGNED_IN, status)

    fun setUserId(id: String) =
        PrefManager.put(Constants.KEY_USER_ID, id)
}