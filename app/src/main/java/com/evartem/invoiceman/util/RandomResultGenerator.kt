package com.evartem.invoiceman.util

import com.evartem.domain.entity.doc.Result
import com.evartem.domain.entity.doc.ResultStatus
import kotlin.random.Random

object RandomResultGenerator {

    val serials = listOf("2384294238", "24323423423", "S2349-SFSDF-445", "GDFGDF-3534534", "S454444FF", "S2349-SFSDF-00F",
        "A342349-SFSDF-445","S20000345445","S20000345446","S20000345447","S20000345448","S20000345400")
    val comments = listOf(
        "Broken packaging",
        "Missing manual",
        "No serial",
        "There's oil inside the box and it looks like the box was repackaged somewhere along the way"
    )

    fun generate(): Result {
        val status = if (Random.nextBoolean()) ResultStatus.COMPLETED else ResultStatus.FAILED

        return Result(
            status, serials[Random.nextInt(serials.size)],
            if (status == ResultStatus.FAILED || Random.nextBoolean())
                comments[Random.nextInt(comments.size)]
            else null
        , 0)
    }
}