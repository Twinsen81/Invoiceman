package com.evartem.invoiceman.util

import android.view.View
import androidx.annotation.CheckResult
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.OnClickListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

@CheckResult
fun <T : IItem<*, *>> FastAdapter<T>.itemClicks(): Observable<T> {
    return FastAdapterClickObservable(this)
}

private class FastAdapterClickObservable<T : IItem<*, *>>(
    private val adapter: FastAdapter<T>
) : Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {
        val listener = Listener(adapter, observer)
        observer.onSubscribe(listener)
        adapter.withOnClickListener(listener)
    }

    private class Listener<T2 : IItem<*, *>>(
        private val adapter: FastAdapter<in T2>,
        private val observer: Observer<in T2>
    ) : MainThreadDisposable(), OnClickListener<T2> {

        override fun onClick(v: View?, adapter: IAdapter<T2>, item: T2, position: Int): Boolean {
            if (!isDisposed) {
                observer.onNext(item)
                return true
            }
            return false
        }

        override fun onDispose() {
            adapter.withOnClickListener(null)
        }
    }
}