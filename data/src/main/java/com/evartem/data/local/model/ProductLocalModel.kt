package com.evartem.data.local.model

import io.realm.RealmList
import io.realm.RealmObject

open class ProductLocalModel(
    var id: Int = 0,
    var article: String = "",
    var description: String = "",
    var quantity: Int = 0,
    var articleScanRequired: Boolean = true,
    var hasSerialNumber: Boolean = true,
    var serialNumberScanRequired: Boolean = true,
    var equalSerialNumbersAreOk: Boolean = false,
    var results: RealmList<ResultLocalModel> = RealmList(),
    var serialNumberPattern: String? = null
): RealmObject() {

    fun insertOrUpdateResult(newResult: ResultLocalModel) {
        val resultToUpdate = results.find { it.id == newResult.id }
        if (resultToUpdate == null) {
            results.add(newResult)
        } else {
            val indexToUpdate = results.indexOf(resultToUpdate)
            results[indexToUpdate] = newResult
        }
    }

    fun deleteResult(result: ResultLocalModel) {
        results.removeAll { it.id == result.id }
    }

    fun deleteAllResults() {
        results.clear()
    }


    fun sortResults() {
        results.sortWith(compareBy {it.id})
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        //if (javaClass != other?.javaClass) return false // Will always be false, since Realm changes the class type

        other as ProductLocalModel

        if (id != other.id) return false
        if (article != other.article) return false
        if (description != other.description) return false
        if (quantity != other.quantity) return false
        if (articleScanRequired != other.articleScanRequired) return false
        if (hasSerialNumber != other.hasSerialNumber) return false
        if (serialNumberScanRequired != other.serialNumberScanRequired) return false
        if (equalSerialNumbersAreOk != other.equalSerialNumbersAreOk) return false
        if (results != other.results) return false
        if (serialNumberPattern != other.serialNumberPattern) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + article.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + quantity
        result = 31 * result + articleScanRequired.hashCode()
        result = 31 * result + hasSerialNumber.hashCode()
        result = 31 * result + serialNumberScanRequired.hashCode()
        result = 31 * result + equalSerialNumbersAreOk.hashCode()
        result = 31 * result + results.hashCode()
        result = 31 * result + (serialNumberPattern?.hashCode() ?: 0)
        return result
    }
}