package com.example.chatapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.dataclass.MsgItem

class ChatViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<MsgItem>>()
    val messages: LiveData<List<MsgItem>> = _messages

    init {
        // Initialize with some initial messages
        _messages.value = listOf(
            MsgItem("Hello", true),
            MsgItem("Hello", true),
            MsgItem("Hello", true),
            MsgItem("Hi", isMine = true),
            MsgItem("Hru", isMine = true),
            MsgItem("Vanakkam", isMine = true),
            MsgItem("Bonjour", isMine = true),
            MsgItem("Namaskaram", isMine = true),
            MsgItem("swagatham", isMine = true, isSelected = true),
            MsgItem("Yes, come on", isMine = true),
            MsgItem("Welcome", isMine = true, isSelected = true),
            MsgItem("Aarambikkalama", isMine = true),
            MsgItem("Hello", false, "Bot")
        )
    }

    fun addMessage(message: MsgItem) {
        val currentMessages = _messages.value.orEmpty()
        _messages.value = currentMessages + message
    }

    fun selection(message: MsgItem) {
        val currentList = _messages.value ?: emptyList()
        val updatedList = currentList.map {
            if (it == message) {
                Log.d("flag", "selection: current ${it.isSelected}")
                it.copy(isSelected = !it.isSelected)
            } else
                it
        }
        _messages.value = updatedList
    }

    fun delete() {
        val currentList = _messages.value ?: emptyList()
        val updatedList = currentList.filter { !it.isSelected }
        _messages.value = updatedList
    }
}
