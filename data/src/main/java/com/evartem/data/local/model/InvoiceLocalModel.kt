package com.evartem.data.local.model

import io.realm.RealmList
import io.realm.RealmObject

open class InvoiceLocalModel(
    var id: String = "",
    var number: Int = 0,
    var date: String = "",
    var seller: String = "",
    var products: RealmList<ProductLocalModel> = RealmList(),
    var processedByUser: String? = null,
    var scanCopyUrl: String? = null,
    var comment: String? = null
): RealmObject()