package com.evartem.data.local.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductLocalModel(
    @PrimaryKey var id: Int = 0,
    var article: String = "",
    var description: String = "",
    var quantity: Int = 0,
    var articleScanRequired: Boolean = true,
    var hasSerialNumber: Boolean = true,
    var serialNumberScanRequired: Boolean = true,
    var equalSerialNumbersAreOk: Boolean = false,
    var results: List<ResultLocalModel> = RealmList(),
    var serialNumberPattern: String? = null
): RealmObject()