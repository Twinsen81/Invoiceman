package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import androidx.core.os.bundleOf

data class InvoicesFragmentOptions(
    val navigateToDetailOnClick: Boolean = true,
    val filterTypeInProgress: Boolean = false,
    val reloadDataOnResume: Boolean = true
) {

    companion object {
        private const val NAV_TO_DETAIL_ON_CLICK = "navigateToDetailOnClick"
        private const val FILTER_TYPE_IN_PROGRESS = "filterTypeInProgress"
        private const val RELOAD_DATA_ON_RESUME = "ReloadDataOnResume"

        fun fromBundle(bundle: Bundle) =
            InvoicesFragmentOptions(
                bundle.getBoolean(NAV_TO_DETAIL_ON_CLICK),
                bundle.getBoolean(FILTER_TYPE_IN_PROGRESS),
                bundle.getBoolean(RELOAD_DATA_ON_RESUME)
            )
    }

    fun toBundle(): Bundle =
        bundleOf(
            NAV_TO_DETAIL_ON_CLICK to navigateToDetailOnClick,
            FILTER_TYPE_IN_PROGRESS to filterTypeInProgress,
            RELOAD_DATA_ON_RESUME to reloadDataOnResume
        )
}