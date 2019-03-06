package com.evartem.domain.interactor

import io.reactivex.Observable

abstract class UseCase<Param, Result>(private val schedulers: Schedulers) {

    internal abstract fun buildObservable(param: Param?): Observable<Result>

    fun execute(param: Param? = null): Observable<Result> =
            buildObservable(param)
                .subscribeOn(schedulers.subscribeOn)
                .observeOn(schedulers.observeOn, true)

}