package com.evartem.domain.entity.doc

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.Rule



class ProductTest {

    @Rule @JvmField
    val exception: ExpectedException = ExpectedException.none()

    //private val product1 = Product(1, "6ES7654-6CL03-4KF0", "H-series PLC", 1)

    private val product1 = Product(2, "6ES7322-1BL00-0AA0", "32 DI module", 3)
    private val product1SerialNumber1 = "123456"
    private val product1SerialNumber2 = "234567"
    private val product1SerialNumber3 = "345678"

    @Test
    fun `Correct results should complete processing successfully`() {

        // GIVEN correct results
        // WHEN add them to the Product
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber2)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber3)

        // SHOULD complete processing successfully
        assertTrue(product1.isProcessingFinishedSuccessfully)
    }


    @Test
    fun `More results than expected should throw an exception`() {
        // GIVEN a completely processed product (with the expected quantity of results)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber2)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber3)

        // WHEN add more results
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        product1.addResult(ResultStatus.COMPLETED, "000000")
    }

    /*

    TEST     private fun getNextResultId() =


     private fun checkResultIsCorrect(status: ResultStatus, serial: String?, comment: String?) {
        checkTheNumberOfResultsIsNotExceeded()
        when (status) {
            ResultStatus.COMPLETED -> {
                if (hasSerialNumber) {
                    checkHasSerialNumber(serial)
                    if (!equalSerialNumbersAreOk)
                        checkSerialIsUnique(serial)
                    if (serialNumberPattern != null)
                        checkSerialMatchesPattern(serial ?: "")
                }
            }
            ResultStatus.FAILED -> {
                checkFailedResultHasComment(comment)
            }
        }
    }

    //region RESULT CONSISTENCY CHECKS
    private fun checkHasSerialNumber(serial: String?) {
        if (serial == null || serial.isEmpty())
            throw IllegalArgumentException("This product must have a serial number")
    }

    private fun checkSerialIsUnique(serial: String?) {
        result.forEach { if (it.serial == serial) throw IllegalArgumentException("A duplicate serial number") }
    }

    private fun checkSerialMatchesPattern(serial: String) {
        val result = serialRegex?.matches(serial)
        if (result == null || !result)
            throw IllegalArgumentException("The serial number doesn't match the provided pattern")
    }

    private fun checkFailedResultHasComment(comment: String?) {
        if (comment == null || comment.isEmpty())
            throw IllegalArgumentException("A failed processing attempt must include a comment about the failure")
    }
*/
}