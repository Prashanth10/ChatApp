package com.example.chatapp

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AppLifecycleCallback : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        App.isAppInForeground = true
    }

    override fun onActivityPaused(activity: Activity) {
        App.isAppInForeground = false
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}
