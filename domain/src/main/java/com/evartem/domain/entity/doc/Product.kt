package com.evartem.domain.entity.doc

/**
 * A product or a service listed in an [Invoice].
 *
 * @property id the product's id on the back-end
 * @property article the product's order number (the manufacturer's identifier of the product)
 * @property description a short description of the product
 * @property quantity the quantity of this kind of product in the invoice
 * @property articleScanRequired the product's article must be scanned from the packaging and must match the [article]
 * property. This ensures that the packaging being processed corresponds to this product
 * @property hasSerialNumber this product has a serial number and it must be entered
 * @property serialNumberScanRequired the serial number must be scanned, not entered manually. Manual input
 * (e.g. when scanning fails for some reason) is possible only for users with the proper permissions
 * @property equalSerialNumbersAreOk products can have the same serial numbers (since some products have only
 * a batch number, not a distinct serial number)
 * @property serialNumberPattern an optional [Regex] pattern that the serial numbers must be tested against
 * @property result the list that contains the result of processing this product by a warehouse employee - serial
 * numbers and statuses of accepted packagings
 */
data class Product(
    val id: Int,
    val article: String,
    val description: String,
    val quantity: Int,
    val articleScanRequired: Boolean = true,
    val hasSerialNumber: Boolean = true,
    val serialNumberScanRequired: Boolean = true,
    val equalSerialNumbersAreOk: Boolean = false,
    private val result: MutableList<Result> = mutableListOf(),
    val serialNumberPattern: String? = null
) {

    private val serialRegex: Regex? by lazy { serialNumberPattern?.toRegex() }

    fun getResult(): List<Result> = result

    fun addResult(
        status: ResultStatus,
        serial: String? = null,
        comment: String? = null
    ) {
        val trimmedSerial = serial?.trim()
        val trimmedComment = comment?.trim()
        checkResultIsCorrect(status, trimmedSerial, trimmedComment)
        result.add(Result(status, trimmedSerial, trimmedComment, getNextResultId()))
    }

    /**
     * Checks that the provided result contains consistent data according to this product properties.
     * The function throws [IllegalArgumentException] in case it finds any inconsistencies.
     * These checks must be enforced on the UI level and thus should always pass successfully here.
     * They are carried out only to prevent inconsistent data in the model.
     * A thrown exception from this function indicates a problem on the UI level.
     */
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

    private fun checkTheNumberOfResultsIsNotExceeded() {
        if (result.size >= quantity)
            throw IllegalArgumentException("The number of processed packagings exceeds the product number in the invoice")
    }
    //endregion

    private fun getNextResultId() =
        (result
            .map { result -> result.id }
            .fold(0) { maxId, currentId -> if (currentId > maxId) currentId else maxId }) + 1


    fun deleteResult(resultId: Int) {
        val resultToDelete = result.find { it.id == resultId }
            ?: throw IllegalArgumentException("Unable to find the result with the id: $resultId")
        result.remove(resultToDelete)
    }

    fun updateResult(newResult: Result) {
        val resultToUpdate = result.find { it.id == newResult.id }
            ?: throw IllegalArgumentException("Unable to find the result with the id: ${newResult.id}")
        val indexToUpdate = result.indexOf(resultToUpdate)
        result[indexToUpdate] = newResult
    }

    val isProcessingFinished
        get() = result.size == quantity

    val isProcessingFinishedWithErrors
        get() = result.size == quantity && result.any { it.status == ResultStatus.FAILED }

    val isProcessingFinishedSuccessfully
        get() = result.size == quantity && result.all { it.status == ResultStatus.COMPLETED }

}