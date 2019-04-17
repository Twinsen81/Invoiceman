package com.evartem.domain.entity.doc

/**
 * Represents an invoice - a document issued by a seller to the buyer that indicates the quantities and costs of the
 * products or services ([Product]) provided by the seller.
 *
 * The class represents only the basic information from a real invoice since it's purpose is not to edit or process
 * the invoice data but to identify one particular invoice among others.
 *
 * @property id the document's id on the back-end (UUID)
 * @property number the document's number
 * @property date the document's date
 * @property seller the name of the company that issued this document
 * @property processedByUser the ID of the user that has accepted this invoice for processing
 * @property scanCopyUrl an optional url to the image with a scanned copy of the document (e.g. JPG or PDF)
 * @property comment an optional comment from the person who added this document to the database
 * @property products the [Product]s listed in this invoice
 */
data class Invoice (
    val id: String,
    val number: Int,
    val date: String,
    val seller: String,
    val products: List<Product>,
    var processedByUser: String? = null,
    val scanCopyUrl: String? = null,
    val comment: String? = null
) {
    val isProcessingStarted
        get() = products.any { it.isProcessingStarted }

    val isProcessingFinishedWithErrors
        get() = isProcessingFinished && products.any { it.isProcessingFinishedWithErrors }

    val isProcessingFinishedSuccessfully
        get() = products.all { it.isProcessingFinishedSuccessfully }

    val totalProductsQuantity
        get() = products.sumBy { it.quantity }

    val totalProductsQuantityFinished
        get() = products.sumBy { it.totalQuantityFinished }

    val isProcessingFinished
        get() = products.all { it.isProcessingFinished }
}
