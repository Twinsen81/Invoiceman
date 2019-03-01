package com.evartem.domain.entity.doc

/**
 * The result of processing a packaging. If the corresponding product requires (via [Product.hasSerialNumber]) a result
 * to have a serial number, then this result must contain a non-null number in the [serial] property.
 * Unless the property [Product.equalSerialNumbersAreOk] is true, the serial numbers must be unique for this kind of
 * product.
 * If the serial number is not required, then the result just confirms the acceptance of the product packaging via
 * the [status] = [ResultStatus.COMPLETED]
 *
 * @property status the status of the scanning/accepting operation
 * @property serial an optional serial number of the packaging being processed
 * @property comment an optional comment about this result. The comment is obligatory if serial number scanning fails, i.e.
 * [status] = [ResultStatus.FAILED]
 * @property id the id that identifies this result among others in the [Product.result].
 */
data class Result(
    val status: ResultStatus,
    val serial: String?,
    val comment: String?,
    val id: Int
)