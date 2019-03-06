package com.evartem.data.local.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Invoice")
data class InvoiceLocalModel(
    @PrimaryKey val id: String,
    val number: Int,
    val date: String,
    val seller: String,
    val products: List<ProductLocalModel>,
    val scanCopyUrl: String? = null,
    val comment: String? = null
)