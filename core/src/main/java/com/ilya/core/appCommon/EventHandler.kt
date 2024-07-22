package com.ilya.core.appCommon

interface EventHandler<in T> {
    fun handleEvent(event: T)
}