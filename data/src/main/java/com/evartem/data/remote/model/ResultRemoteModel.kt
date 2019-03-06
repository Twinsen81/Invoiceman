package com.evartem.data.remote.model

data class ResultRemoteModel (
    val status: Int,
    val serial: String?,
    val comment: String?,
    val id: Int
)