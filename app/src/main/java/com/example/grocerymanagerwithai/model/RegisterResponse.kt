package com.example.grocerymanagerwithai.model

data class RegisterResponse(
    val status: Boolean,
    val message: String,
    val data: UserData?
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String
)
