package com.ilya.core.appCommon.base

interface BaseFactory<in T, out R> {
    fun newInstance(initializationData: T): R
}