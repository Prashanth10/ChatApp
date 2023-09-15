package com.example.chatapp.utils

import android.widget.Toast
import com.example.chatapp.App

object Utils {
    private var toast: Toast? = null

    fun showToast(message: String) {
        if (toast != null) toast?.cancel()
        toast = Toast.makeText(App.ctx, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}