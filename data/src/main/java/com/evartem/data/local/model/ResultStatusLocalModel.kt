package com.evartem.data.local.model

import io.realm.RealmObject

open class ResultStatusLocalModel(
    /**
     * status = 0 = [com.evartem.domain.entity.doc.ResultStatus.COMPLETED]
     * status = 1 = [com.evartem.domain.entity.doc.ResultStatus.FAILED]
     */
    var status: Int = 0
) : RealmObject()