package com.evartem.invoiceman.util

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.bumptech.glide.Glide
import com.evartem.invoiceman.R
import kotlinx.android.synthetic.main.dialog_status.*

/**
 * A modal dialog with an infinite progress bar to display when long running (e.g. network) operations are
 * under way.
 */
class StatusDialog(context: Context) {
    private val dialog: Dialog by lazy { createDialog(context) }

    private fun createDialog(context: Context): Dialog {
        val dialog = Dialog(context, R.style.StatusDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_status)
        Glide.with(dialog.context)
            .load(R.drawable.loading_infinity)
            .placeholder(R.drawable.loading_infinity_stat)
            .into(dialog.loading_image)
        return dialog
    }

    /**
     * Show the dialog with the specified [text] as the title.
     */
    fun show(text: String) {
        dialog.loading_text.text = text
        dialog.show()
    }

    fun hide() {
        dialog.dismiss()
    }
}