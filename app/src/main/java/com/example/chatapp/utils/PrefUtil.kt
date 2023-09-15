package com.example.chatapp.utils

import com.example.chatapp.constants.Constants
import com.example.chatapp.prefs.PrefManager

object PrefUtil {

    fun getSignedIn() = PrefManager.getBoolean(Constants.KEY_IS_SIGNED_IN)

    fun setSignedIn(status: Boolean) =
        PrefManager.put(Constants.KEY_IS_SIGNED_IN, status)

    fun getUserId() = PrefManager.getString(Constants.KEY_USER_ID)

    fun setUserId(id: String) =
        PrefManager.put(Constants.KEY_USER_ID, id)

    fun getUserName() = PrefManager.getString(Constants.KEY_NAME)
    fun setUserName(name: String) =
        PrefManager.put(Constants.KEY_NAME, name)
}