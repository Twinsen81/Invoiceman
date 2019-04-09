package com.evartem.invoiceman.invoices.mvi.util

import com.evartem.domain.interactor.Schedulers
import io.reactivex.Scheduler

class TestSchedulers: Schedulers {
    override val subscribeOn: Scheduler
        get() = io.reactivex.schedulers.Schedulers.single()
    override val observeOn: Scheduler
        get() = io.reactivex.schedulers.Schedulers.single()
}