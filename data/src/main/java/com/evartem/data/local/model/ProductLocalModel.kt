package com.evartem.data.local.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Product")
data class ProductLocalModel(
    @PrimaryKey val id: Int,
    val article: String,
    val description: String,
    val quantity: Int,
    val articleScanRequired: Boolean = true,
    val hasSerialNumber: Boolean = true,
    val serialNumberScanRequired: Boolean = true,
    val equalSerialNumbersAreOk: Boolean = false,
    private val results: List<ResultLocalModule> = listOf(),
    val serialNumberPattern: String? = null
)