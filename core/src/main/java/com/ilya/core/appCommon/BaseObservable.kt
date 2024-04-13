package com.ilya.core.appCommon

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@OptIn(InternalCoroutinesApi::class)
abstract class BaseObservable<T> {

    protected val observers = mutableListOf<T>()
    protected val mutex = Any()

    fun addObserver(observer: T) {
        synchronized(mutex) {
            observers += observer
        }
    }

    fun removeObserver(observer: T) {
        synchronized(mutex) {
            observers -= observer
        }
    }

    protected abstract fun notifyObservers()

}