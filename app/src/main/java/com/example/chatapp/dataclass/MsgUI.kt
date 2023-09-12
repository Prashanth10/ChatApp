package com.example.chatapp.dataclass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

data class MsgUI(
    val arrangement: Arrangement.Horizontal,
    val alignment: Alignment.Horizontal,
    val cardColor: Color,
    val userNameColor: Color,
    val msgColor: Color
)
