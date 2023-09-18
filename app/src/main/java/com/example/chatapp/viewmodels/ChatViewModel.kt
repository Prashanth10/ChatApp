package com.example.chatapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.dataclass.MsgItem

class ChatViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<MsgItem>>()
    val messages: LiveData<List<MsgItem>> = _messages

    /*init {
        _messages.value = listOf(
            MsgItem("Hello", true),
            MsgItem("Hey", true),
            MsgItem("Eww", true),
            MsgItem("Hi", true),
            MsgItem("Hru", true),
            MsgItem("Vanakkam", true),
            MsgItem("Bonjour", true),
            MsgItem("Namaskaram", true),
            MsgItem("swagatham", true),
            MsgItem("Yes, come on", true),
            MsgItem("Welcome", true),
            MsgItem("Compose is fun", true),
            MsgItem("Hello, How can I help you?", false, "Bot")
        )
    }*/

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
