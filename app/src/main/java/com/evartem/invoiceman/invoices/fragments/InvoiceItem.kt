package com.evartem.invoiceman.invoices.fragments

import android.view.View
import com.evartem.domain.entity.doc.Invoice
import com.evartem.invoiceman.R
import com.evartem.invoiceman.invoices.fragments.InvoicesAvailableFragment.Companion.INVOICE_ITEM_TYPE_BASIC
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_invoice.view.*

class InvoiceItem(val invoice: Invoice) : AbstractItem<InvoiceItem, InvoiceItem.ViewHolder>() {

    override fun getType(): Int = INVOICE_ITEM_TYPE_BASIC

    override fun getViewHolder(v: View): InvoiceItem.ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_invoice

    class ViewHolder(view: View) : FastAdapter.ViewHolder<InvoiceItem>(view) {
        override fun unbindView(item: InvoiceItem) {
        }

        override fun bindView(item: InvoiceItem, payloads: MutableList<Any>) {
            itemView.invoice_seller.text = item.invoice.seller
            itemView.invoice_number.text = item.invoice.number.toString()
            itemView.invoice_date.text = item.invoice.date
            itemView.invoice_qty.text = "0"

            itemView.invoice_comment.apply {
                text = item.invoice.comment ?: ""
                visibility = if (item.invoice.comment != null) View.VISIBLE else View.GONE
            }

            if (item.invoice.products.isNotEmpty()) {
                itemView.invoice_products_card.visibility = View.VISIBLE

                itemView.invoice_product1_article.text = item.invoice.products[0].article
                itemView.invoice_product1_description.text = item.invoice.products[0].description
                itemView.invoice_product1_qty.text = item.invoice.products[0].quantity.toString()

                if (item.invoice.products.size >= 2) {
                    itemView.invoice_product2.visibility = View.VISIBLE
                    itemView.invoice_product2_article.text = item.invoice.products[1].article
                    itemView.invoice_product2_description.text = item.invoice.products[1].description
                    itemView.invoice_product2_qty.text = item.invoice.products[1].quantity.toString()
                } else
                    itemView.invoice_product2.visibility = View.GONE
            } else
                itemView.invoice_products_card.visibility = View.GONE
        }
    }
}