package com.evartem.domain.entity.doc

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class ProductTest {

    @Rule
    @JvmField
    val exception: ExpectedException = ExpectedException.none()

    lateinit var product1: Product
    lateinit var product2: Product
    lateinit var product1SerialNumber1: String
    lateinit var product1SerialNumber2: String
    lateinit var product1SerialNumber3: String

    @Before
    fun setup() {
        product1 = Product(1, "6ES7322-1BL00-0AA0", "32 DI module", 3)
        product2 = Product(2, "6ES7322-1BL00-0AA0", "32 DI module", 1, serialNumberPattern = "S[A-Z\\d]{10}")
        product1SerialNumber1 = "123456"
        product1SerialNumber2 = "SABC4567890"
        product1SerialNumber3 = "345678"
    }

    @Test
    fun `A result without a serial should be OK`() {
        // GIVEN a product that doesn't require serial numbers
        val product = Product(3, "6ES7322-1BL00-0AA0", "32 DI module", 1, hasSerialNumber = false)

        // WHEN add a result with a null serial number
        product.addResult(ResultStatus.COMPLETED)

        // SHOULD complete processing successfully
        assertTrue(product.isProcessingFinishedSuccessfully)
    }

    @Test
    fun `Results with same serials should be OK`() {
        // GIVEN a product that doesn't require unique serial numbers
        val product = Product(3, "6ES7322-1BL00-0AA0", "32 DI module", 2, equalSerialNumbersAreOk = true)

        // WHEN add results with the same serial number
        product.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
        product.addResult(ResultStatus.COMPLETED, product1SerialNumber1)

        // SHOULD complete processing successfully
        assertTrue(product.isProcessingFinishedSuccessfully)
    }

    @Test
    fun `A serial number that matches the pattern should be accepted`() {
        // GIVEN a product that requires serial numbers to match a particular pattern
        // WHEN add a result with the serial number that matches
        product2.addResult(ResultStatus.COMPLETED, product1SerialNumber2)

        // SHOULD complete processing successfully
        assertTrue(product2.isProcessingFinishedSuccessfully)
    }

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
    fun `Deleting a result by ID should remove it from the results`() {
        // GIVEN a product with results
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
        val result = product1.addResult(ResultStatus.COMPLETED, product1SerialNumber2)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber3)

        // WHEN delete a result by ID
        product1.deleteResult(result.id)

        // SHOULD remove it from the results list
        assertTrue(product1.getResults().none { it.id == result.id })
    }

    @Test
    fun `Deleting a result with an unknown ID should throw an exception`() {
        // GIVEN a product with results
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)

        // WHEN delete by an unknown ID
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        product1.deleteResult(9999)
    }

    @Test
    fun `Updating a result should complete successfully`() {
        // GIVEN a product with results
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
        val result = product1.addResult(ResultStatus.COMPLETED, product1SerialNumber2)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber3)

        // WHEN update a result
        val result2 = result.copy(serial = "01234567890")
        product1.updateResult(result2)

        // SHOULD successfully update it in the results list
        assertTrue(product1.getResults().filter { it.serial == result2.serial && it.id == result2.id}.count() == 1)
    }

    @Test
    fun `Updating a non-existent result should throw an exception`() {
        // GIVEN a product with results
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)

        // WHEN update a non-existent result
        // SHOULD throw an IllegalArgumentException
        val result = Result(ResultStatus.COMPLETED, "123", "comment", 9999)
        exception.expect(IllegalArgumentException::class.java)
        product1.updateResult(result)
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

    @Test
    fun `Shuffling results should keep their IDs unique`() {
        // GIVEN a product with a few results
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
        val result2 = product1.addResult(ResultStatus.COMPLETED, product1SerialNumber2)
        val result3 = product1.addResult(ResultStatus.COMPLETED, product1SerialNumber3)

        // WHEN delete and add results
        product1.deleteResult(result2.id)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber2)
        product1.deleteResult(result3.id)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber3)

        // SHOULD keep IDs in the results collection unique
        assertTrue(
            product1.getResults()
                .groupBy { it.id }
                .map { groupedById -> groupedById.value.size }
                .none { groupSize -> groupSize > 1 })
    }

    @Test
    fun `An empty serial number should throw an exception`() {
        // GIVEN a product that requires non-empty serial numbers
        // WHEN add a result with an empty serial number
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        product1.addResult(ResultStatus.COMPLETED, "")
    }

    @Test
    fun `A null serial number should throw an exception`() {
        // GIVEN a product that requires non-empty serial numbers
        // WHEN add a result with a null serial number
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        product1.addResult(ResultStatus.COMPLETED, null)
    }

    @Test
    fun `A non-unique serial number should throw an exception`() {
        // GIVEN a product that requires unique serial numbers and that already contains some results
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)

        // WHEN add a result with the same serial number
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
    }

    @Test
    fun `A failed result without a comment should throw an exception`() {
        // GIVEN a product that requires serial numbers
        // WHEN add a failed result without a comment
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        product1.addResult(ResultStatus.FAILED)
    }

    @Test
    fun `A failed result without a serial should be OK`() {
        // GIVEN a product that requires serial numbers
        // WHEN add a failed result with a comment but without a serial number
        product1.addResult(ResultStatus.FAILED, comment = "Damaged packaging")
        product1.addResult(ResultStatus.FAILED, comment = "Damaged packaging")
        product1.addResult(ResultStatus.FAILED, comment = "Damaged packaging")

        // SHOULD complete processing
        assertTrue(product1.isProcessingFinished)
        assertTrue(product1.isProcessingFinishedWithErrors)
    }

    @Test
    fun `A result with a serial number not matching the pattern should throw an exception`() {
        // GIVEN a product that requires serial numbers to match a particular pattern
        // WHEN add a result with a serial number that doesn't match the pattern
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        product2.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
    }

    @Test
    fun `An incorrect regex pattern should throw an exception`() {
        // GIVEN a product with incorrect regex pattern
        val product = Product(3, "6ES7322-1BL00-0AA0", "32 DI module", 1, serialNumberPattern = "}{)(S[A-Z\\d]{D}")

        // WHEN add a result with a serial number that doesn't match the pattern
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        product.addResult(ResultStatus.COMPLETED, product1SerialNumber2)
    }

}