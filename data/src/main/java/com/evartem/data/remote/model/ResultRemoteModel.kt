package com.evartem.data.remote.model

/**
 * A DTO class for domain Result objects used in exchange with the server.
 */
data class ResultRemoteModel(
    val status: Int,
    val serial: String?,
    val comment: String?,
    val id: Int
)