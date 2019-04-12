package com.evartem.domain.interactor

import io.reactivex.Observable

/**
 * A base abstract class that defines how use case should be constructed (as Observables).
 *
 * @param Param the type of parameters passed to the use case
 * @param Result the type of the result returned after the use case execution
 * @property schedulers the schedulers that the use case's Observable is subscribed and observed on
 */
abstract class UseCase<Param, Result>(private val schedulers: Schedulers) {

    /**
     * Concrete use cases override this to create themselves.
     */
    internal abstract fun buildObservable(param: Param?): Observable<Result>

    /**
     * Execute the built use case on the specified schedulers.
     */
    fun execute(param: Param? = null): Observable<Result> =
        buildObservable(param)
            .subscribeOn(schedulers.subscribeOn)
            .observeOn(schedulers.observeOn, true)
}