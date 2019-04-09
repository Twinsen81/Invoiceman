package com.evartem.invoiceman.util

import com.evartem.domain.interactor.Schedulers
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

class SubscribeIoObserveUi: Schedulers {
    override val subscribeOn: Scheduler
        get() = io.reactivex.schedulers.Schedulers.io()
    override val observeOn: Scheduler
        get() = AndroidSchedulers.mainThread()
}