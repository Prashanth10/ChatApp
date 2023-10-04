package com.example.chatapp.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.utils.PrefUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            ChatAppTheme(darkTheme = true) {
                LoginApplication()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun LoginApplication() {
        val navController = rememberNavController()
        var firstPage = "login_page"
        if (PrefUtil.getSignedIn())
            firstPage = "chat_page"
        NavHost(navController = navController, startDestination = firstPage, builder = {
            composable("login_page", content = { LoginPage(navController = navController) })
            composable("register_page", content = { RegisterPage(navController = navController) })
            composable("chat_page", content = { ChatPage(navController = navController) },
//                enterTransition = {slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn()}
                enterTransition = { expandVertically(initialHeight = { 100 }) + fadeIn() }
            )
        })
    }
}