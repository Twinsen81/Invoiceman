package com.evartem.invoiceman.invoices

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.evartem.invoiceman.R

class InvoicesPagerAdapter(fragmentManager: FragmentManager, private val context: Context) :
    FragmentPagerAdapter(fragmentManager) {

    private val pages = listOf(
        Pair(R.string.invoices_tab_available, InvoicesAvailableFragment()),
        Pair(R.string.invoices_tab_in_progress, InvoicesInProgressFragment())
    )

    override fun getItem(position: Int): Fragment = pages[position].second

    override fun getCount() = pages.size

    override fun getPageTitle(position: Int): CharSequence? =
        context.resources.getString(pages[position].first)
}