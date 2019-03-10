package com.evartem.data.local.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class InvoiceLocalModel(
    @PrimaryKey var id: String = "",
    var number: Int = 0,
    var date: String = "",
    var seller: String = "",
    var products: RealmList<ProductLocalModel> = RealmList(),
    var processedByUser: String? = null,
    var scanCopyUrl: String? = null,
    var comment: String? = null
): RealmObject() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InvoiceLocalModel

        if (id != other.id) return false
        if (number != other.number) return false
        if (date != other.date) return false
        if (seller != other.seller) return false
        if (products != other.products) return false
        if (processedByUser != other.processedByUser) return false
        if (scanCopyUrl != other.scanCopyUrl) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + number
        result = 31 * result + date.hashCode()
        result = 31 * result + seller.hashCode()
        result = 31 * result + products.hashCode()
        result = 31 * result + (processedByUser?.hashCode() ?: 0)
        result = 31 * result + (scanCopyUrl?.hashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        return result
    }
}