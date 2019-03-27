package com.evartem.invoiceman.invoices.fragments

import android.view.View
import com.evartem.invoiceman.R
import com.evartem.invoiceman.invoices.fragments.InvoicesAvailableFragment.Companion.INVOICE_ITEM_TYPE_INVISIBLE
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class InvisibleItem : AbstractItem<InvisibleItem, InvisibleItem.ViewHolder>() {

    override fun getType(): Int = INVOICE_ITEM_TYPE_INVISIBLE

    override fun getViewHolder(v: View): InvisibleItem.ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_invisible_footer

    class ViewHolder(view: View) : FastAdapter.ViewHolder<InvisibleItem>(view) {
        override fun unbindView(item: InvisibleItem)  = Unit
        override fun bindView(item: InvisibleItem, payloads: MutableList<Any>) = Unit
    }
}