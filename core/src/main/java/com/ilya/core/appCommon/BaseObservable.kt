package com.ilya.core.appCommon

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@OptIn(InternalCoroutinesApi::class)
abstract class BaseObservable<T> {

    protected val listeners = mutableListOf<T>()
    protected val mutex = Any()

    fun addListener(observer: T) {
        synchronized(mutex) {
            listeners += observer
        }
    }

    fun removeListener(listener: T) {
        synchronized(mutex) {
            listeners -= listener
        }
    }

    protected abstract fun notifyListeners()

}