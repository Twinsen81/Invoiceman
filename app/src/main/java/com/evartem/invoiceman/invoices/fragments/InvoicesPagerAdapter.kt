package com.evartem.invoiceman.invoices.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class InvoicesPagerAdapter(fragmentManager: FragmentManager, private val pages: List<Pair<String, Fragment>>) :
    FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = pages[position].second

    override fun getCount() = pages.size

    override fun getPageTitle(position: Int) = pages[position].first
}