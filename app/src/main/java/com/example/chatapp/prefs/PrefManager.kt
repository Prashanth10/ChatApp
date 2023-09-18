package com.example.chatapp.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.chatapp.App

object PrefManager {
    private var sharedPref: SharedPreferences? =
        App.ctx.getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
    private var prefEditor: SharedPreferences.Editor? = null
    private var bulkUpdate = false

    fun clearPreferences() {
        sharedPref?.edit()?.clear()?.apply()
    }

    fun getLong(key: String, default: Long = -1L): Long {
        return sharedPref?.getLong(key, default) ?: default
    }

    fun getInt(key: String, default: Int = -1): Int {
        return sharedPref?.getInt(key, default) ?: default
    }

    fun getString(key: String, default: String = ""): String {
        return sharedPref?.getString(key, default) ?: default
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPref?.getBoolean(key, default) ?: default
    }

    fun put(key: String, data: Long) {
        doEdit()
        prefEditor?.putLong(key, data)
        doCommit()
    }

    fun put(key: String, data: Int) {
        doEdit()
        prefEditor?.putInt(key, data)
        doCommit()
    }

    fun put(key: String, data: String) {
        doEdit()
        prefEditor?.putString(key, data)
        doCommit()
    }

    fun put(key: String, data: Boolean) {
        doEdit()
        prefEditor?.putBoolean(key, data)
        doCommit()
    }

    private fun doEdit() {
        if (!bulkUpdate && prefEditor == null) prefEditor = sharedPref?.edit()
    }

    private fun doCommit() {
        if (!bulkUpdate && prefEditor != null) {
            prefEditor?.commit()
            prefEditor = null
        }
    }

    fun edit() {
        bulkUpdate = true
        prefEditor = sharedPref?.edit()
    }

    fun commit() {
        bulkUpdate = false
        prefEditor?.commit()
        prefEditor = null
    }
}