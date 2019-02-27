package com.evartem.domain.entity.auth

data class Group(
    val id: Int,
    val description: String,
    val permissions: List<Permission>
)