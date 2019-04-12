package com.evartem.data.remote.model

/**
 * A DTO class for domain Product objects used in exchange with the server.
 */
data class ProductRemoteModel(
    val id: Int,
    val article: String,
    val description: String,
    val quantity: Int,
    val articleScanRequired: Boolean = true,
    val hasSerialNumber: Boolean = true,
    val serialNumberScanRequired: Boolean = true,
    val equalSerialNumbersAreOk: Boolean = false,
    val results: List<ResultRemoteModel> = listOf(),
    val serialNumberPattern: String? = null
)