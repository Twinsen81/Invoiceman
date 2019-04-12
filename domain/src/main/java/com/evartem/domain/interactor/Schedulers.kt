package com.evartem.domain.interactor

import io.reactivex.Scheduler

/**
 * An interface for injecting schedulers into use cases.
 * Define the schedulers that the use case's Observable is subscribed an observed on.
 */
interface Schedulers {

    val subscribeOn: Scheduler

    val observeOn: Scheduler
}