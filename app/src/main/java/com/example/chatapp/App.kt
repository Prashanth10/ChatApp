package com.example.chatapp

import android.app.Application

class App : Application() {
    companion object {
        lateinit var ctx: Application
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this
    }
}