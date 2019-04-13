package com.evartem.domain.util

import com.evartem.domain.interactor.Schedulers
import io.reactivex.Scheduler

/**
 * The [Schedulers] interface implementation to run tests on RxJava data flows.
 * trampoline() executes tasks one by one (queued) in a single thread.
 */
class TestSchedulers: Schedulers {
    override val subscribeOn: Scheduler
        get() = io.reactivex.schedulers.Schedulers.trampoline()
    override val observeOn: Scheduler
        get() = io.reactivex.schedulers.Schedulers.trampoline()
}