package com.evartem.data.remote.model

/**
 * A DTO class for domain Invoice objects used in exchange with the server.
 */
data class InvoiceRemoteModel(
    val id: String,
    val number: Int,
    val date: String,
    val seller: String,
    val products: List<ProductRemoteModel>,
    val processedByUser: String? = null,
    val scanCopyUrl: String? = null,
    val comment: String? = null
)