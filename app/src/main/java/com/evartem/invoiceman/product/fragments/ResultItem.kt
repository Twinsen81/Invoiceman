package com.evartem.invoiceman.product.fragments

import android.view.View
import com.evartem.domain.entity.doc.Result
import com.evartem.invoiceman.R
import com.evartem.invoiceman.product.fragments.ProductDetailFragment.Companion.RESULT_ITEM_TYPE_BASIC
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_result.view.*

class ResultItem(val result: Result) : AbstractItem<ResultItem, ResultItem.ViewHolder>() {

        override fun getType(): Int = RESULT_ITEM_TYPE_BASIC

        override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

        override fun getLayoutRes(): Int = R.layout.item_result

        class ViewHolder(view: View) : FastAdapter.ViewHolder<ResultItem>(view) {
            override fun unbindView(item: ResultItem) = Unit

            override fun bindView(item: ResultItem, payloads: MutableList<Any>) {

                itemView.result_serial.text = item.result.serial

                if (item.result.comment != null) {
                    itemView.result_comment.text = item.result.comment
                    itemView.result_comment.visibility = View.VISIBLE
                } else {
                    itemView.result_comment.text = ""
                    itemView.result_comment.visibility = View.GONE
                }
            }
        }
}