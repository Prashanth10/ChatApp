package com.example.chatapp.dataclass

data class RegisterFormData(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)
