package com.evartem.data.repository

import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.remote.api.InvoiceService

class InvoiceRepository(private val localDataSource: InvoiceLocalDataSource,
                        private val remoteDataSource: InvoiceService) {
}