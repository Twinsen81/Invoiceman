package com.evartem.data.local.model

import io.realm.RealmObject

/**
 * A DTO class for storing locally (caching) the domain ResultStatus objects
 * Implemented as a Realm object.
 */
open class ResultStatusLocalModel(
    /**
     * status = 0 = [com.evartem.domain.entity.doc.ResultStatus.COMPLETED]
     * status = 1 = [com.evartem.domain.entity.doc.ResultStatus.FAILED]
     */
    var status: Int = 0
) : RealmObject() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        // if (javaClass != other?.javaClass) return false // Will always be false, since Realm changes the class type

        other as ResultStatusLocalModel

        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        return status
    }
}