package com.evartem.invoiceman.invoices.mvi.util

import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState

class TestDataUiState {
    val initialLoading = InvoicesUiState(isLoading = true)
    val emptyList = InvoicesUiState()
    val emptyListRefreshing = InvoicesUiState(isRefreshing = true)
    val twoInvoicesRefreshing =
        InvoicesUiState(invoices = TestDataEntity().invoice1And2.toMutableList(), isRefreshing = true)
    val twoInvoices = InvoicesUiState(invoices = TestDataEntity().invoice1And2.toMutableList())
    val twoInvoicesStartSearch = InvoicesUiState(
        invoices = TestDataEntity().invoice1And2.toMutableList(), searchViewOpen = true, setFocusToSearchView = true
    )
    val twoInvoicesSearchQuery = InvoicesUiState(
        invoices = TestDataEntity().invoice1And2.toMutableList(), searchViewOpen = true, searchRequest = "test"
    )
    val twoInvoicesSortByNumber = InvoicesUiState(
        invoices = TestDataEntity().invoice1And2.toMutableList(), sorting = InvoicesEvent.Sort.SortBy.NUMBER
    )
}