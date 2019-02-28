package com.evartem.domain.entity.doc

data class Result(
    val id: Int,
    val status: ResultStatus,
    val serial: String?,
    val comment: String?
)