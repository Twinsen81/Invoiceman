package com.evartem.invoiceman.invoice.fragments

import android.view.View
import androidx.core.content.ContextCompat
import com.evartem.domain.entity.doc.Product
import com.evartem.invoiceman.R
import com.evartem.invoiceman.invoice.fragments.InvoiceDetailFragment.Companion.PRODUCT_ITEM_TYPE_BASIC
import com.evartem.invoiceman.invoice.fragments.InvoiceDetailFragment.Companion.processingStatusBackground
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.*

class ProductItem(val product: Product) : AbstractItem<ProductItem, ProductItem.ViewHolder>() {

    override fun getIdentifier() = product.id.toLong()

    override fun getType(): Int = PRODUCT_ITEM_TYPE_BASIC

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_product

    class ViewHolder(override val containerView: View) : FastAdapter.ViewHolder<ProductItem>(containerView),
        LayoutContainer {

        override fun unbindView(item: ProductItem) = Unit

        override fun bindView(item: ProductItem, payloads: MutableList<Any>) {
            product_article.text = item.product.article
            product_description.text = item.product.description

            product_qty.text = item.product.quantity.toString()
            if (item.product.isProcessingStarted && !item.product.isProcessingFinished) {
                product_qty_done.text = item.product.getResults().size.toString()
                product_qty_done.visibility = View.VISIBLE
                product_qty_of.visibility = View.VISIBLE
            } else {
                product_qty_done.visibility = View.GONE
                product_qty_of.visibility = View.GONE
            }

            item_product_card.setCardBackgroundColor(
                when {
                    item.product.isProcessingFinishedSuccessfully ->
                        processingStatusBackground.finishedWithoutErrors
                    item.product.isProcessingFinishedWithErrors ->
                        processingStatusBackground.finishedWithErrors
                    item.product.isProcessingStarted ->
                        processingStatusBackground.startedButNotFinishedYet
                    else -> processingStatusBackground.notEvenStarted
                }
            )

            product_article_scan_required.visibility =
                if (item.product.articleScanRequired) View.VISIBLE else View.GONE
            product_has_serial.visibility =
                if (item.product.hasSerialNumber) View.VISIBLE else View.GONE
            product_serial_scan_required.visibility =
                if (item.product.serialNumberScanRequired) View.VISIBLE else View.GONE
            product_serial_same.visibility =
                if (item.product.equalSerialNumbersAreOk) View.VISIBLE else View.GONE
            product_serial_pattern_used.visibility =
                if (item.product.serialNumberPattern != null) View.VISIBLE else View.GONE
        }
    }
}