package com.evartem.invoiceman.util

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.evartem.invoiceman.R
import com.evartem.invoiceman.product.fragments.ProductDetailFragment

@GlideModule
class TheAppGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.apply {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(System.currentTimeMillis().toShort()))
        }
    }
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = activity.currentFocus ?: View(activity)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Defines and caches background colors for processing statuses: finished with errors, finished without errors,
 * not yet finished
 */
class ProcessingStatusBackground(context: Context) {

    val finishedWithErrors
        get() = finishedWithErrorsInternal
    private var finishedWithErrorsInternal = ContextCompat.getColor(context, R.color.gotFailedBackground)

    val finishedWithoutErrors
        get() = finishedWithoutErrorsInternal
    private var finishedWithoutErrorsInternal = ContextCompat.getColor(context, R.color.resultCompletedBackground)

    val startedButNotFinishedYet
        get() = startedButNotFinishedYetInternal
    private var startedButNotFinishedYetInternal = ContextCompat.getColor(context, R.color.inProgressBackground)

    val notEvenStarted
        get() = notEvenStartedInternal
    private var notEvenStartedInternal = ContextCompat.getColor(context, R.color.semiTransparentBackground)
}
