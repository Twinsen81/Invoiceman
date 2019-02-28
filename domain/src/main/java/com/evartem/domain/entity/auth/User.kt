package com.evartem.domain.entity.auth

data class User (
    val id: String,
    val fullName: String,
    val position: String,
    val group: Group,
    val status: UserStatus
)