package com.evartem.data.remote.model

data class InvoiceRemoteModel (
    val id: String,
    val number: Int,
    val date: String,
    val seller: String,
    val products: List<ProductRemoteModel>,
    val scanCopyUrl: String? = null,
    val comment: String? = null
)