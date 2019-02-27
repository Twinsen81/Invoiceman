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
 * @property serialNumberScanRequired the serial number must be scanned, not entered manually. Manual input is possible
 * only for users with the proper permissions (e.g. when scanning fails for some reason)
 */
data class Product(
    val id: Int,
    val article: String,
    val description: String,
    val quantity: Int,
    val articleScanRequired: Boolean,
    val hasSerialNumber: Boolean,
    val serialNumberScanRequired: Boolean
)