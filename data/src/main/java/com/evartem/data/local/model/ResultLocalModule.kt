package com.evartem.data.local.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.evartem.domain.entity.doc.ResultStatus

@Entity(tableName = "Result")
data class ResultLocalModule(
    @PrimaryKey val id: Int,
    @Embedded val status: ResultStatus,
    val serial: String?,
    val comment: String?
)