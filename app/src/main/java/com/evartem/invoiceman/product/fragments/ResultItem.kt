package com.evartem.invoiceman.product.fragments

import android.view.View
import androidx.core.content.ContextCompat
import com.evartem.domain.entity.doc.Result
import com.evartem.domain.entity.doc.ResultStatus
import com.evartem.invoiceman.R
import com.evartem.invoiceman.product.fragments.ProductDetailFragment.Companion.RESULT_ITEM_TYPE_BASIC
import com.evartem.invoiceman.product.fragments.ProductDetailFragment.Companion.processingStatusBackground
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_result.*

class ResultItem(val result: Result) : AbstractItem<ResultItem, ResultItem.ViewHolder>() {

    override fun getIdentifier() = result.id.toLong()

    override fun getType(): Int = RESULT_ITEM_TYPE_BASIC

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_result

    class ViewHolder(override val containerView: View) : FastAdapter.ViewHolder<ResultItem>(containerView),
        LayoutContainer {

        override fun unbindView(item: ResultItem) = Unit

        override fun bindView(item: ResultItem, payloads: MutableList<Any>) {

            result_serial.text = item.result.serial

            if (item.result.comment != null) {
                result_comment.text = item.result.comment
                result_comment.visibility = View.VISIBLE
            } else {
                result_comment.text = ""
                result_comment.visibility = View.GONE
            }

            item_result_card.setCardBackgroundColor(
                if (item.result.status == ResultStatus.COMPLETED)
                    processingStatusBackground.resultCompleted
                else
                    processingStatusBackground.resultFailed
            )
        }
    }
}