package com.evartem.invoiceman.invoices.fragments

import android.annotation.SuppressLint
import android.view.View
import com.evartem.domain.entity.doc.Invoice
import com.evartem.invoiceman.R
import com.evartem.invoiceman.invoices.fragments.InvoicesFragment.Companion.INVOICE_ITEM_TYPE_BASIC
import com.evartem.invoiceman.invoices.fragments.InvoicesFragment.Companion.processingStatusBackground
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_invoice.*

class InvoiceItem(val invoice: Invoice) : AbstractItem<InvoiceItem, InvoiceItem.ViewHolder>() {

    override fun getIdentifier() = invoice.hashCode().toLong()

    override fun getType(): Int = INVOICE_ITEM_TYPE_BASIC

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_invoice

    class ViewHolder(override val containerView: View) : FastAdapter.ViewHolder<InvoiceItem>(containerView),
        LayoutContainer {

        override fun unbindView(item: InvoiceItem) = Unit

        @SuppressLint("SetTextI18n")
        override fun bindView(item: InvoiceItem, payloads: MutableList<Any>) {
            invoice_seller.text = item.invoice.seller
            invoice_number.text = item.invoice.number.toString()
            invoice_date.text = item.invoice.date

            invoice_qty.text =
                if (item.invoice.isProcessingStarted && !item.invoice.isProcessingFinished)
                    "${item.invoice.totalProductsQuantityFinished}/${item.invoice.totalProductsQuantity}"
                else
                    item.invoice.totalProductsQuantity.toString()

            invoice_comment.apply {
                text = item.invoice.comment ?: ""
                visibility = if (item.invoice.comment != null) View.VISIBLE else View.GONE
            }

            item_invoice_card.background.setTint(
                when {
                    item.invoice.isProcessingFinishedSuccessfully ->
                        processingStatusBackground.finishedWithoutErrors
                    item.invoice.isProcessingFinishedWithErrors ->
                        processingStatusBackground.finishedWithErrors
                    item.invoice.isProcessingStarted ->
                        processingStatusBackground.startedButNotFinishedYet
                    else -> processingStatusBackground.notEvenStarted
                }
            )

            if (item.invoice.products.isNotEmpty()) {
                invoice_products_list.visibility = View.VISIBLE

                invoice_product1_article.text = item.invoice.products[0].article
                invoice_product1_description.text = item.invoice.products[0].description
                invoice_product1_qty.text = item.invoice.products[0].quantity.toString()

                if (item.invoice.products.size >= 2) {
                    invoice_product2.visibility = View.VISIBLE
                    invoice_product2_article.text = item.invoice.products[1].article
                    invoice_product2_description.text = item.invoice.products[1].description
                    invoice_product2_qty.text = item.invoice.products[1].quantity.toString()
                } else
                    invoice_product2.visibility = View.GONE
            } else
                invoice_products_list.visibility = View.GONE
        }
    }
}