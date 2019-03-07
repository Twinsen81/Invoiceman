package com.evartem.data.local.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ResultLocalModel(
    @PrimaryKey var id: Int = 0,
    var status: ResultStatusLocalModel? = null,
    val serial: String? = null,
    val comment: String? = null
) : RealmObject()