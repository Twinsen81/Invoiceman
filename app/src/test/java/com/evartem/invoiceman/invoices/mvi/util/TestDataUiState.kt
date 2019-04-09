package com.evartem.invoiceman.invoices.mvi.util

import com.evartem.invoiceman.invoices.mvi.InvoicesUiState

class TestDataUiState {
    val initialLoading = InvoicesUiState(isLoading = true)
    val emptyList = InvoicesUiState()
    val emptyListRefreshing = InvoicesUiState(isRefreshing = true)
    val twoInvoicesRefreshing = InvoicesUiState(invoices = TestDataEntity().invoice1And2.toMutableList(), isRefreshing = true)
    val twoInvoices = InvoicesUiState(invoices = TestDataEntity().invoice1And2.toMutableList())
}