package com.evartem.data.remote.model

data class ProductRemoteModel(
    val id: Int,
    val article: String,
    val description: String,
    val quantity: Int,
    val articleScanRequired: Boolean = true,
    val hasSerialNumber: Boolean = true,
    val serialNumberScanRequired: Boolean = true,
    val equalSerialNumbersAreOk: Boolean = false,
    private val results: List<ResultRemoteModel> = listOf(),
    val serialNumberPattern: String? = null
)