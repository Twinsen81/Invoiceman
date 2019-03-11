package com.evartem.data.local.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ResultLocalModel(
    var id: Int = 0,
    var status: ResultStatusLocalModel? = null,
    var serial: String? = null,
    var comment: String? = null
) : RealmObject() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        //if (javaClass != other?.javaClass) return false // Will always be false, since Realm changes the class type

        other as ResultLocalModel

        if (id != other.id) return false
        if (status != other.status) return false
        if (serial != other.serial) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (serial?.hashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        return result
    }
}