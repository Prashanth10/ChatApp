package com.example.chatapp.dataclass

import java.util.Calendar
import java.util.Date

data class MsgItem(
    val content: String,
    val isMine: Boolean,
    val userName: String = "Me",
    val timeStamp: Date = Calendar.getInstance().time,
    var isSelected: Boolean = false
)
