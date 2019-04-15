package com.evartem.invoiceman.invoice.fragments

import android.view.View
import com.evartem.domain.entity.doc.Product
import com.evartem.invoiceman.R
import com.evartem.invoiceman.invoice.fragments.InvoiceDetailFragment.Companion.PRODUCT_ITEM_TYPE_BASIC
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_product.view.*

class ProductItem(val product: Product) : AbstractItem<ProductItem, ProductItem.ViewHolder>() {

    override fun getType(): Int = PRODUCT_ITEM_TYPE_BASIC

    override fun getViewHolder(v: View): ProductItem.ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_product

    class ViewHolder(view: View) : FastAdapter.ViewHolder<ProductItem>(view) {
        override fun unbindView(item: ProductItem) = Unit

        override fun bindView(item: ProductItem, payloads: MutableList<Any>) {
            itemView.product_article.text = item.product.article
            itemView.product_description.text = item.product.description
            itemView.product_qty.text = item.product.quantity.toString()

            itemView.product_article_scan_required.visibility =
                    if (item.product.articleScanRequired) View.VISIBLE else View.GONE
            itemView.product_has_serial.visibility =
                    if (item.product.hasSerialNumber) View.VISIBLE else View.GONE
            itemView.product_serial_scan_required.visibility =
                    if (item.product.serialNumberScanRequired) View.VISIBLE else View.GONE
            itemView.product_serial_same.visibility =
                    if (item.product.equalSerialNumbersAreOk) View.VISIBLE else View.GONE
        }
    }
}